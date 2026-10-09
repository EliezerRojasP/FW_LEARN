package com.example.four_ways_learn.admin

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ItemAdminRecordBinding
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import com.example.four_ways_learn.ui.LessonSections
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class AdminListActivity : BaseActivity() {
    override val requiresAdmin = true
    private lateinit var resource: String
    private val keys = mapOf(
        "cursos" to "id_curso",
        "topicos" to "id_topico",
        "lecciones" to "id_leccion",
        "preguntas" to "id_pregunta",
        "usuarios" to "id_usuario"
    )
    private var editing = false
    private var editingItem: JsonObject? = null
    private var query = ""
    private var filter = 0
    private var generation = 0
    private val fields = linkedMapOf<String, TextInputEditText>()
    private val sections = linkedMapOf<String, TextInputEditText>()
    private var selectedSection = 0
    private var parents = listOf<JsonObject>()
    private var parentSpinner: Spinner? = null
    private var roleSpinner: Spinner? = null
    private var correctSpinner: Spinner? = null
    private var stateSwitch: SwitchMaterial? = null
    private var options = listOf<TextInputEditText>()

    private fun JsonObject.value(key: String): String =
        get(key)?.takeUnless { it.isJsonNull }?.asString.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resource = intent.getStringExtra("recurso").orEmpty()
        screen(intent.getStringExtra("titulo") ?: getString(R.string.admin_fallback_title))
        query = savedInstanceState?.getString("query").orEmpty(); filter =
            savedInstanceState?.getInt("filter") ?: 0
        if (resource !in keys && resource !in listOf("estadisticas", "resultados")) {
            message(getString(R.string.error_unknown_module)); return
        }
        if (savedInstanceState?.getBoolean("editing") == true) {
            val item = savedInstanceState.getString("editing_item")
                ?.let { JsonParser.parseString(it).asJsonObject }
            edit(item, savedInstanceState.getBundle("draft"))
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (editing) load() else {
                    isEnabled = false; onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume(); if (!editing && (resource in keys || resource in listOf(
                "estadisticas",
                "resultados"
            ))
        ) load()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("query", query); outState.putInt(
            "filter",
            filter
        ); outState.putBoolean("editing", editing)
        if (editing) {
            outState.putString("editing_item", editingItem?.toString())
            val draft = Bundle()
            fields.forEach { (key, edit) ->
                if (key != "password") draft.putString(
                    key,
                    edit.text.toString()
                )
            }
            sections.forEach { (key, edit) ->
                draft.putString(
                    "section_$key",
                    edit.text.toString()
                )
            }
            parentSpinner?.let { spinner ->
                parents.getOrNull(spinner.selectedItemPosition)?.let { parent ->
                    val key = when (resource) {
                        "topicos" -> "id_curso"; "lecciones" -> "id_topico"; else -> "id_leccion"
                    }
                    draft.putString(key, parent.value(key))
                }
            }
            draft.putInt("role", roleSpinner?.selectedItemPosition ?: 0)
            draft.putInt("correct", correctSpinner?.selectedItemPosition ?: 0)
            draft.putBoolean("active", stateSwitch?.isChecked ?: true)
            draft.putInt("section", selectedSection)
            options.forEachIndexed { index, edit ->
                draft.putString(
                    "option_$index",
                    edit.text.toString()
                )
            }
            outState.putBundle("draft", draft)
        }
        super.onSaveInstanceState(outState)
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(panel.windowToken, 0)
        panel.clearFocus()
    }

    private fun load() {
        editing = false; hideKeyboard(); bottomActions()
        shell.headerTitle.text =
            intent.getStringExtra("titulo") ?: getString(R.string.admin_fallback_title)
        val current = ++generation
        request(RetrofitClient.api.adminList(resource)) { data ->
            if (current != generation) return@request
            panel.removeAllViews()
            if (resource == "estadisticas") {
                val stats = data?.takeIf { it.isJsonObject }?.asJsonObject ?: return@request
                panel.addView(text(getString(R.string.module_stats), 27, true))
                panel.addView(muted(getString(R.string.admin_stats_hint), 14))
                val labels = listOf(
                    "alumnos" to R.string.stat_students,
                    "cursos" to R.string.stat_courses,
                    "lecciones" to R.string.stat_lessons,
                    "intentos" to R.string.stat_attempts,
                    "promedio" to R.string.admin_stats_average
                )
                labels.forEach { (key, label) ->
                    box { inside ->
                        inside.addView(muted(getString(label), 14))
                        val value = if (key == "promedio") percent(
                            stats.value(key).toDoubleOrNull() ?: 0.0
                        ) else stats.value(key)
                        inside.addView(text(value, 30, true))
                    }
                }
                bottomActions(button(getString(R.string.action_refresh)) { load() }); return@request
            }
            val items =
                data?.takeIf { it.isJsonArray }?.asJsonArray?.map { it.asJsonObject }.orEmpty()
            val search = input(
                getString(if (resource == "usuarios") R.string.search_users else R.string.search_records),
                query
            )
            if (resource in keys) {
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                val labels =
                    listOf(R.string.filter_all, R.string.filter_active, R.string.filter_inactive)
                labels.forEachIndexed { index, label ->
                    row.addView(secondary(getString(label)) { filter = index; redraw() }.apply {
                        textSize = 12f; minWidth = 0; setPadding(dp(4), 0, dp(4), 0)
                        if (filter == index) {
                            backgroundTintList =
                                ColorStateList.valueOf(color(R.color.fw_blue)); setTextColor(color(R.color.fw_on_primary))
                        }
                    }, LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(6) })
                }
                panel.addView(row)
            }
            val count = muted("", 12); panel.addView(count)
            val list = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }; panel.addView(list)
            fun draw() {
                list.removeAllViews()
                val filtered = items.filter { item ->
                    val matches = query.isBlank() || listOf(
                        "nombre",
                        "apellido",
                        "usuario",
                        "titulo",
                        "enunciado",
                        "alumno",
                        "descripcion"
                    ).any { item.value(it).contains(query, true) }
                    val active = item.value("estado") == "1"
                    matches && (resource == "resultados" || filter == 0 || (filter == 1 && active) || (filter == 2 && !active))
                }
                count.text = getString(R.string.filter_result_count, filtered.size)
                if (filtered.isEmpty()) list.addView(muted(getString(if (items.isEmpty()) R.string.empty_records else R.string.no_search_results)))
                filtered.forEach { item ->
                    if (resource == "resultados") box(list) { inside ->
                        inside.addView(text(item.value("titulo"), 18, true))
                        inside.addView(muted(item.value("alumno"), 14))
                        inside.addView(
                            badge(
                                percent(
                                    item.value("porcentaje").toDoubleOrNull() ?: 0.0
                                )
                            )
                        )
                        inside.addView(
                            muted(
                                getString(
                                    R.string.admin_grade,
                                    item.value("puntaje"),
                                    item.value("total_preguntas"),
                                    item.value("porcentaje"),
                                    item.value("fecha_resultado")
                                ), 13
                            )
                        )
                    } else {
                        val card = ItemAdminRecordBinding.inflate(layoutInflater, list, true)
                        val title = item.value("usuario").ifBlank {
                            item.value("nombre").ifBlank {
                                item.value("titulo").ifBlank { item.value("enunciado") }
                            }
                        }
                        card.avatar.text = title.take(1).uppercase(); card.recordTitle.text = title
                        card.recordDetail.text = if (resource == "usuarios") getString(
                            R.string.admin_user_detail,
                            getString(if (item.value("id_rol") == "1") R.string.role_admin else R.string.role_student),
                            getString(
                                R.string.admin_user_identity,
                                item.value("nombre"),
                                item.value("apellido")
                            ).trim()
                        )
                        else item.value("descripcion").ifBlank { getString(R.string.action_edit) }
                        card.recordDetail.maxLines = 2
                        card.recordState.setText(if (item.value("estado") == "1") R.string.state_active else R.string.state_inactive)
                        card.recordState.setTextColor(color(if (item.value("estado") == "1") R.color.fw_green else R.color.fw_muted))
                        card.root.setOnClickListener { edit(item) }
                    }
                }
            }
            search.doAfterTextChanged { query = it.toString(); draw() }
            redraw = { draw(); updateFilterColors() }
            draw()
            if (resource in keys) bottomActions(button(getString(if (resource == "usuarios") R.string.action_create_user else R.string.action_create_record)) {
                edit(
                    null
                )
            }.apply { setIconResource(R.drawable.ic_fw_plus) })
        }
    }

    private var redraw: () -> Unit = {}
    private fun updateFilterColors() {
        // The filter row is the second panel child after the search field.
        val row = panel.getChildAt(1) as? LinearLayout ?: return
        for (index in 0 until row.childCount) {
            val view = row.getChildAt(index) as? com.google.android.material.button.MaterialButton
                ?: continue
            view.backgroundTintList =
                ColorStateList.valueOf(color(if (index == filter) R.color.fw_blue else R.color.fw_blue_soft))
            view.setTextColor(color(if (index == filter) R.color.fw_on_primary else R.color.fw_blue))
        }
    }

    private fun spinner(
        label: String,
        labels: List<String>,
        selected: Int = 0,
        container: LinearLayout
    ): Spinner {
        container.addView(text(label, 14, true))
        val view = Spinner(this).apply {
            minimumHeight = dp(48)
            adapter = ArrayAdapter(
                this@AdminListActivity,
                android.R.layout.simple_spinner_dropdown_item,
                labels
            )
            if (labels.isNotEmpty()) setSelection(selected.coerceIn(0, labels.lastIndex))
        }
        val card = MaterialCardView(this).apply {
            radius = dp(12).toFloat(); cardElevation = 0f; strokeWidth = dp(1); strokeColor =
            color(R.color.fw_border)
            setCardBackgroundColor(color(R.color.fw_card)); addView(view)
        }
        container.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        return view
    }

    private fun edit(item: JsonObject?, draft: Bundle? = null) {
        editing = true; editingItem = item; ++generation
        panel.removeAllViews(); bottomActions(); status.visibility = View.GONE; retry.visibility =
            View.GONE
        fields.clear(); sections.clear(); parents = emptyList(); options =
            emptyList(); roleSpinner = null; correctSpinner = null; parentSpinner = null
        val heading = when (resource) {
            "usuarios" -> if (item == null) R.string.editor_new_user else R.string.editor_user
            "lecciones" -> if (item == null) R.string.editor_new_lesson else R.string.editor_lesson
            else -> if (item == null) R.string.create_record_title else R.string.edit_record_title
        }
        shell.headerTitle.setText(heading)
        var form = panel
        box { form = it }
        fun field(
            key: String,
            label: Int,
            multiline: Boolean = false,
            numeric: Boolean = false,
            password: Boolean = false
        ) {
            val kind = when {
                numeric -> InputType.TYPE_CLASS_NUMBER
                password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                multiline -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                else -> InputType.TYPE_CLASS_TEXT
            }
            val value = draft?.getString(key) ?: item?.value(key) ?: if (key == "orden") "1" else ""
            fields[key] =
                input(getString(label), value, kind, form).apply { if (multiline) minLines = 3 }
        }

        val parentInfo = when (resource) {
            "topicos" -> Triple("cursos", "id_curso", R.string.label_course)
            "lecciones" -> Triple("topicos", "id_topico", R.string.label_topic)
            "preguntas" -> Triple("lecciones", "id_leccion", R.string.label_lesson)
            else -> null
        }
        if (parentInfo != null) {
            val current = generation
            val courseSelector = if (resource == "lecciones") spinner(
                getString(R.string.label_course),
                listOf(getString(R.string.loading)),
                container = form
            ) else null
            val selector = spinner(
                getString(parentInfo.third),
                listOf(getString(R.string.loading)),
                container = form
            )
            parentSpinner = selector
            val desiredId = draft?.getString(parentInfo.second) ?: item?.value(parentInfo.second)
            fun fill(records: List<JsonObject>) {
                parents = records
                val labels = records.map { parent ->
                    val title = parent.value("nombre").ifBlank { parent.value("titulo") }
                    if (parent.value("estado") == "0") getString(
                        R.string.inactive_record,
                        title
                    ) else title
                }
                selector.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    labels.ifEmpty { listOf(getString(R.string.empty_parent_records)) })
                val selected = records.indexOfFirst { it.value(parentInfo.second) == desiredId }
                if (records.isNotEmpty()) selector.setSelection(if (selected < 0) 0 else selected)
            }
            request(RetrofitClient.api.adminList(parentInfo.first)) parentsRequest@{ data ->
                if (!editing || generation != current) return@parentsRequest
                val records =
                    data?.takeIf { it.isJsonArray }?.asJsonArray?.map { it.asJsonObject }.orEmpty()
                if (courseSelector == null) fill(records) else {
                    request(RetrofitClient.api.adminList("cursos")) coursesRequest@{ response ->
                        if (!editing || generation != current) return@coursesRequest
                        val courses =
                            response?.takeIf { it.isJsonArray }?.asJsonArray?.map { it.asJsonObject }
                                .orEmpty()
                        courseSelector.adapter =
                            ArrayAdapter(
                                this, android.R.layout.simple_spinner_dropdown_item,
                                courses.map { it.value("nombre") }
                                    .ifEmpty { listOf(getString(R.string.empty_parent_records)) })
                        val desiredCourse =
                            records.firstOrNull { it.value("id_topico") == desiredId }
                                ?.value("id_curso")
                        val position =
                            courses.indexOfFirst { it.value("id_curso") == desiredCourse }
                                .coerceAtLeast(0)
                        courseSelector.setSelection(position)
                        fun update(position: Int) {
                            fill(records.filter {
                                it.value("id_curso") == courses.getOrNull(position)
                                    ?.value("id_curso")
                            })
                        }
                        update(position)
                        courseSelector.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    update(position)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    fill(emptyList())
                                }
                            }
                    }
                }
            }
        }
        when (resource) {
            "cursos" -> {
                field("nombre", R.string.field_course_name); field(
                    "descripcion",
                    R.string.field_description,
                    multiline = true
                )
            }

            "topicos" -> {
                field("nombre", R.string.field_topic_name); field(
                    "descripcion",
                    R.string.field_description,
                    multiline = true
                ); field("orden", R.string.field_order, numeric = true)
            }

            "lecciones" -> {
                field("titulo", R.string.field_lesson_title); field(
                    "orden",
                    R.string.field_order,
                    numeric = true
                )
            }

            "preguntas" -> {
                field("enunciado", R.string.field_question, multiline = true); field(
                    "orden",
                    R.string.field_order,
                    numeric = true
                )
            }

            "usuarios" -> {
                field("nombre", R.string.field_first_name); field(
                    "apellido",
                    R.string.field_last_name
                ); field("usuario", R.string.field_username)
                field(
                    "password",
                    if (item == null) R.string.field_new_password else R.string.field_edit_password,
                    password = true
                )
                roleSpinner = spinner(
                    getString(R.string.field_role),
                    listOf(getString(R.string.role_student), getString(R.string.role_admin)),
                    draft?.getInt("role") ?: if (item?.value("id_rol") == "1") 1 else 0,
                    form
                )
            }
        }
        if (resource == "lecciones") {
            val parsed = LessonSections.parse(item?.value("contenido").orEmpty())
            panel.addView(muted(getString(R.string.editor_sections_hint), 13))
            val groups =
                List(3) { LinearLayout(this).apply { orientation = LinearLayout.VERTICAL } }
            val specs = listOf(
                Triple("content", R.string.field_section_content, parsed.content),
                Triple("example", R.string.field_section_example, parsed.example),
                Triple("practice", R.string.field_section_practice, parsed.practice),
                Triple("explanation", R.string.field_section_explanation, parsed.explanation)
            )
            specs.forEachIndexed { index, (key, label, value) ->
                val container = groups[index.coerceAtMost(2)]
                sections[key] = input(
                    getString(label), draft?.getString("section_$key") ?: value,
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE, container
                ).apply { minLines = if (index == 3) 3 else 5 }
            }
            selectedSection = (draft?.getInt("section") ?: 0).coerceIn(0, 2)
            fun showSection(index: Int) {
                selectedSection = index; groups.forEachIndexed { i, group ->
                    group.visibility = if (i == index) View.VISIBLE else View.GONE
                }
            }
            panel.addView(
                tabs(
                    listOf(
                        getString(R.string.tab_content),
                        getString(R.string.tab_example),
                        getString(R.string.tab_practice)
                    ), selectedSection
                ) { showSection(it) })
            groups.forEach { panel.addView(it) }; showSection(selectedSection)
        }
        val oldOptions = item?.getAsJsonArray("alternativas")
        if (resource == "preguntas") {
            val count = maxOf(4, oldOptions?.size() ?: 0)
            options = (0 until count).map { index ->
                input(
                    getString(R.string.option_number, index + 1), draft?.getString("option_$index")
                        ?: oldOptions?.getOrNull(index)?.asJsonObject?.value("texto").orEmpty()
                )
            }
            val correct =
                oldOptions?.indexOfFirst { it.asJsonObject.value("es_correcta") == "1" } ?: 0
            correctSpinner = spinner(
                getString(R.string.field_correct_option),
                (1..count).map { getString(R.string.option_number, it) },
                draft?.getInt("correct") ?: correct.coerceAtLeast(0),
                panel
            )
        }
        box { inside ->
            stateSwitch = SwitchMaterial(this).apply {
                setText(if (resource == "lecciones") R.string.lesson_active else R.string.field_active)
                setTextColor(color(R.color.fw_ink)); minimumHeight = dp(48)
                isChecked = draft?.getBoolean("active") ?: (item?.value("estado") != "0")
            }
            inside.addView(stateSwitch); inside.addView(
            muted(
                getString(R.string.editor_state_hint),
                12
            )
        )
        }
        if (item != null) panel.addView(outlined(getString(R.string.action_disable_record)) {
            AlertDialog.Builder(this).setTitle(R.string.action_disable_record)
                .setMessage(R.string.disable_record_confirmation)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_disable) { _, _ ->
                    request(
                        RetrofitClient.api.adminDisable(
                            resource,
                            mapOf(
                                keys.getValue(resource) to item.value(keys.getValue(resource))
                                    .toInt()
                            )
                        )
                    ) { load() }
                }.show()
        }.apply {
            setTextColor(color(R.color.fw_danger)); strokeColor =
            ColorStateList.valueOf(color(R.color.fw_border))
        })
        bottomActions(
            outlined(getString(R.string.action_cancel)) { load() },
            button(getString(R.string.action_save_changes)) { save(parentInfo) })
        shell.contentScroll.post { shell.contentScroll.scrollTo(0, 0) }
    }

    private fun save(parentInfo: Triple<String, String, Int>?) {
        if (progress.visibility == View.VISIBLE) return
        val data = linkedMapOf<String, Any>()
        for ((key, edit) in fields) {
            val value = edit.text.toString()
            if (key == "orden") {
                val number = value.toIntOrNull()
                if (number == null || number < 1) {
                    message(getString(R.string.error_order_positive)); return
                }
                data[key] = number
            } else data[key] = if (key == "password") value else value.trim()
        }
        if (parentInfo != null) {
            val parent = parents.getOrNull(parentSpinner?.selectedItemPosition ?: -1)
            if (parent == null) {
                message(
                    getString(
                        R.string.wait_parent,
                        getString(parentInfo.third).lowercase()
                    )
                ); return
            }
            data[parentInfo.second] = parent.value(parentInfo.second).toInt()
        }
        if (resource == "usuarios") data["id_rol"] =
            if (roleSpinner?.selectedItemPosition == 1) 1 else 2
        if (resource == "lecciones") {
            val old = LessonSections.parse(editingItem?.value("contenido").orEmpty())
            data["contenido"] = old.compose(
                sections.getValue("content").text.toString(),
                sections.getValue("example").text.toString(),
                sections.getValue("practice").text.toString(),
                sections.getValue("explanation").text.toString()
            )
        }
        if (resource == "preguntas") {
            if (options.any { it.text.toString().isBlank() }) {
                message(getString(R.string.error_options_required)); return
            }
            data["alternativas"] = options.mapIndexed { index, edit ->
                mapOf(
                    "texto" to edit.text.toString().trim(),
                    "es_correcta" to if (index == correctSpinner?.selectedItemPosition) 1 else 0
                )
            }
        }
        data["estado"] = if (stateSwitch?.isChecked == true) 1 else 0
        editingItem?.let {
            data[keys.getValue(resource)] = it.value(keys.getValue(resource)).toInt()
        }
        val call = if (editingItem == null) RetrofitClient.api.adminCreate(
            resource,
            data
        ) else RetrofitClient.api.adminUpdate(resource, data)
        request(call) { load() }
    }

    private fun com.google.gson.JsonArray.getOrNull(index: Int) =
        if (index in 0 until size()) get(index) else null
}

package com.example.four_ways_learn.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.four_ways_learn.MainActivity
import com.example.four_ways_learn.R
import com.example.four_ways_learn.admin.AdminActivity
import com.example.four_ways_learn.admin.AdminListActivity
import com.example.four_ways_learn.admin.AdminContentActivity
import com.example.four_ways_learn.alumno.*
import com.example.four_ways_learn.databinding.ActivityShellBinding
import com.example.four_ways_learn.databinding.ItemProgressCardBinding
import com.example.four_ways_learn.databinding.ItemLessonRowBinding
import com.example.four_ways_learn.model.Leccion
import com.example.four_ways_learn.network.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import kotlin.math.roundToInt

/** Shared XML scaffold. Data-dependent cards are populated from the API. */
open class BaseActivity : AppCompatActivity() {
    protected open val requiresSession = true
    protected open val requiresAdmin = false
    private val calls = mutableListOf<Call<*>>()
    private var pendingRequests = 0
    private var selectingNavigation = false
    protected lateinit var shell: ActivityShellBinding
    protected lateinit var panel: LinearLayout
    protected lateinit var status: TextView
    protected lateinit var progress: ProgressBar
    protected lateinit var retry: MaterialButton
    protected val isAdmin get() = SessionStore.usuario?.rol.equals("ADMIN", ignoreCase = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStore.init(this)
        if (requiresSession && SessionStore.token == null) toLogin()
        else if (requiresAdmin && !isAdmin) finish()
    }
    override fun onResume() {
        super.onResume()
        if (requiresSession && SessionStore.token == null) toLogin()
        else if (requiresAdmin && !isAdmin) finish()
        if (::shell.isInitialized && requiresSession) selectNavigation()
    }
    protected fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
    protected fun color(id: Int) = ContextCompat.getColor(this, id)
    protected fun screen(title: String, back: Boolean = true) {
        shell = ActivityShellBinding.inflate(layoutInflater)
        setContentView(shell.root)
        panel = shell.contentPanel; status = shell.statusMessage; progress = shell.loading; retry = shell.retryButton
        shell.headerTitle.text = title
        shell.backButton.visibility = if (back) View.VISIBLE else View.GONE
        shell.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        shell.brandIcon.visibility = if (back) View.GONE else View.VISIBLE
        shell.adminBadge.visibility = if (isAdmin) View.VISIBLE else View.GONE
        shell.accountButton.text = SessionStore.usuario?.nombre?.take(1)?.uppercase().orEmpty()
        shell.accountButton.setOnClickListener { navigate(AccountActivity::class.java) }
        shell.accountButton.visibility = if (requiresSession) View.VISIBLE else View.GONE
        if (!requiresSession) shell.header.visibility = View.GONE
        setupNavigation()
        shell.contentScroll.isVerticalScrollBarEnabled = false
        shell.contentScroll.isHorizontalScrollBarEnabled = false
        ViewCompat.setOnApplyWindowInsetsListener(shell.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            shell.bottomNavigation.visibility = if (requiresSession && !insets.isVisible(WindowInsetsCompat.Type.ime())) View.VISIBLE else View.GONE
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(shell.root)
    }
    private fun selectNavigation() {
        selectingNavigation = true
        shell.bottomNavigation.selectedItemId = when {
            this is AccountActivity -> R.id.nav_account
            this is AdminContentActivity -> R.id.nav_content
            isAdmin && this is AdminListActivity && intent.getStringExtra("recurso") == "resultados" -> R.id.nav_results
            isAdmin && this is AdminListActivity && intent.getStringExtra("recurso") in listOf("cursos", "topicos", "lecciones", "preguntas") -> R.id.nav_content
            isAdmin -> R.id.nav_panel
            this is ProgresoActivity -> R.id.nav_progress
            this is BienvenidaActivity -> R.id.nav_home
            else -> R.id.nav_courses
        }
        selectingNavigation = false
    }
    private fun setupNavigation() {
        val nav = shell.bottomNavigation
        if (!requiresSession) { nav.visibility = View.GONE; return }
        nav.inflateMenu(if (isAdmin) R.menu.navigation_admin else R.menu.navigation_student)
        selectNavigation()
        nav.setOnItemSelectedListener { item ->
            if (selectingNavigation) return@setOnItemSelectedListener true
            openNavigation(item.itemId)
            true
        }
        nav.setOnItemReselectedListener { item ->
            if (!selectingNavigation) openNavigation(item.itemId)
        }
    }
    private fun openNavigation(id: Int) {
        when (id) {
            R.id.nav_home -> navigate(BienvenidaActivity::class.java)
            R.id.nav_courses -> navigate(CursosActivity::class.java)
            R.id.nav_progress -> if (!isAdmin) navigate(ProgresoActivity::class.java)
            R.id.nav_panel -> navigate(AdminActivity::class.java)
            R.id.nav_content -> if (isAdmin) navigate(AdminContentActivity::class.java)
            R.id.nav_results -> openAdmin("resultados", R.string.module_results)
            R.id.nav_account -> navigate(AccountActivity::class.java)
        }
    }
    private fun openAdmin(resource: String, title: Int) {
        if (this !is AdminListActivity || intent.getStringExtra("recurso") != resource)
            startActivity(Intent(this, AdminListActivity::class.java).putExtra("recurso", resource).putExtra("titulo", getString(title)))
    }
    protected fun navigate(target: Class<out AppCompatActivity>) {
        if (javaClass != target) startActivity(Intent(this, target).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
    protected fun text(value: String, size: Int = 17, bold: Boolean = false): TextView = TextView(this).apply {
        text = value; textSize = size.toFloat(); setTextColor(color(R.color.fw_ink))
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setLineSpacing(dp(3).toFloat(), 1.05f); setPadding(0, dp(4), 0, dp(8))
    }
    protected fun muted(value: String, size: Int = 15) = text(value, size).apply { setTextColor(color(R.color.fw_muted)) }
    protected fun button(value: String, click: () -> Unit): MaterialButton = MaterialButton(this).apply {
        text = value; isAllCaps = false; minHeight = dp(54); cornerRadius = dp(14)
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8); bottomMargin = dp(8) }
        setOnClickListener { click() }
    }
    protected fun secondary(value: String, click: () -> Unit) = button(value, click).apply {
        backgroundTintList = ColorStateList.valueOf(color(R.color.fw_blue_soft)); setTextColor(color(R.color.fw_blue))
    }
    protected fun outlined(value: String, click: () -> Unit) = button(value, click).apply {
        backgroundTintList = ColorStateList.valueOf(color(R.color.fw_card))
        setTextColor(color(R.color.fw_blue)); strokeColor = ColorStateList.valueOf(color(R.color.fw_blue)); strokeWidth = dp(1)
    }
    protected fun bottomActions(vararg actions: MaterialButton) {
        shell.bottomActions.removeAllViews()
        shell.bottomActions.visibility = if (actions.isEmpty()) View.GONE else View.VISIBLE
        actions.forEachIndexed { index, action ->
            action.maxLines = 2
            action.setPadding(dp(10), dp(12), dp(10), dp(12))
            shell.bottomActions.addView(action, LinearLayout.LayoutParams(0, -2, 1f).apply {
                if (index > 0) marginStart = dp(10)
            })
        }
    }
    protected fun box(container: LinearLayout = panel, tone: Int = R.color.fw_card, padding: Int = 18, block: (LinearLayout) -> Unit): MaterialCardView {
        val card = MaterialCardView(this).apply {
            radius = dp(20).toFloat(); cardElevation = 0f; setCardBackgroundColor(color(tone))
            strokeColor = color(R.color.fw_border); strokeWidth = if (tone == R.color.fw_card) dp(1) else 0
        }
        val inside = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(padding), dp(padding), dp(padding), dp(padding)) }
        card.addView(inside); block(inside)
        container.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        return card
    }
    protected fun icon(id: Int, size: Int = 44, tone: Int = R.color.fw_blue_soft) = ImageView(this).apply {
        setImageResource(id); imageTintList = ColorStateList.valueOf(color(R.color.fw_blue))
        background = rounded(tone, 12); setPadding(dp(10), dp(10), dp(10), dp(10))
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        layoutParams = LinearLayout.LayoutParams(dp(size), dp(size))
    }
    protected fun rounded(tone: Int, radius: Int = 12) = GradientDrawable().apply { cornerRadius = dp(radius).toFloat(); setColor(color(tone)) }
    protected fun courseIcon(name: String): Int = when {
        name.contains("datos", true) -> R.drawable.ic_fw_database
        name.contains("web", true) -> R.drawable.ic_fw_web
        name.contains("algoritmo", true) -> R.drawable.ic_fw_grid
        name.contains("inteligencia", true) -> R.drawable.ic_fw_ai
        else -> R.drawable.ic_fw_code
    }
    protected fun card(title: String, detail: String, click: () -> Unit) {
        box { inside -> inside.addView(text(title, 18, true)); inside.addView(muted(detail, 14)) }.apply {
            isClickable = true; isFocusable = true; setOnClickListener { click() }
        }
    }
    protected fun percent(value: Double): String = if (value % 1.0 == 0.0) getString(R.string.percent_whole, value.toInt()) else getString(R.string.percent_decimal, value)
    protected fun progressBar(value: Double): LinearProgressIndicator = LinearProgressIndicator(this).apply {
        isIndeterminate = false; max = 10000; trackThickness = dp(7); trackCornerRadius = dp(4)
        setIndicatorColor(color(R.color.fw_blue)); trackColor = color(R.color.fw_track)
        setProgressCompat((value.coerceIn(0.0, 100.0) * 100).roundToInt(), false)
        contentDescription = getString(R.string.progress_accessibility, percent(value))
        layoutParams = LinearLayout.LayoutParams(-1, dp(12)).apply { topMargin = dp(8); bottomMargin = dp(8) }
    }
    protected fun updateProgress(bar: LinearProgressIndicator, value: Double) {
        bar.setProgressCompat((value.coerceIn(0.0, 100.0) * 100).roundToInt(), false)
        bar.contentDescription = getString(R.string.progress_accessibility, percent(value))
    }
    protected fun badge(value: String): TextView = text(value, 12, true).apply {
        setPadding(dp(10), dp(5), dp(10), dp(5)); setTextColor(color(R.color.fw_green)); background = rounded(R.color.fw_mint)
        layoutParams = LinearLayout.LayoutParams(-2, -2).apply { bottomMargin = dp(8) }
    }
    protected fun progressState(completed: Int, total: Int): String = when {
        total > 0 && completed == total -> getString(R.string.progress_completed)
        completed > 0 -> getString(R.string.progress_in_progress)
        else -> getString(R.string.progress_not_started)
    }
    protected fun progressCard(title: String, detail: String, value: Double, caption: String, state: String, container: LinearLayout = panel, iconRes: Int = R.drawable.ic_fw_book, click: () -> Unit) {
        val card = ItemProgressCardBinding.inflate(layoutInflater, container, true)
        card.courseTitle.text = title; card.courseDetail.text = detail
        card.courseDetail.visibility = if (detail.isBlank()) View.GONE else View.VISIBLE
        card.courseIcon.setImageResource(iconRes); card.percentLabel.text = percent(value)
        card.courseCaption.text = caption; card.courseState.text = state
        updateProgress(card.courseProgress, value)
        card.root.setOnClickListener { click() }
    }
    protected fun lessonRow(lesson: Leccion, container: LinearLayout, click: () -> Unit) {
        val row = ItemLessonRowBinding.inflate(layoutInflater, container, true)
        row.lessonTitle.text = lesson.titulo
        val completed = lesson.completada == 1
        row.lessonStatus.text = getString(if (completed) R.string.state_completed else R.string.state_pending)
        row.lessonStatus.setTextColor(color(if (completed) R.color.fw_green else R.color.fw_muted))
        if (!completed) {
            row.lessonStateIcon.setImageDrawable(null)
            row.lessonStateIcon.setBackgroundResource(R.drawable.bg_pending)
        }
        row.root.setOnClickListener { click() }
    }
    protected fun progressSummary(title: String, completed: Int, total: Int, value: Double) {
        box(tone = R.color.fw_navy, padding = 22) { inside ->
            inside.addView(text(title, 17, true).apply { setTextColor(color(R.color.fw_on_navy)) })
            inside.addView(text(percent(value), 38, true).apply { setTextColor(color(R.color.fw_on_navy)) })
            inside.addView(progressBar(value)); inside.addView(text(getString(R.string.completed_lessons_count, completed, total), 14).apply { setTextColor(color(R.color.fw_on_navy)) })
        }
    }
    protected fun input(label: String, value: String = "", kind: Int = InputType.TYPE_CLASS_TEXT, container: LinearLayout = panel): TextInputEditText {
        val layout = TextInputLayout(this).apply { hint = label; boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE }
        val edit = TextInputEditText(layout.context).apply {
            inputType = kind; setText(value); textSize = 16f; setTextColor(color(R.color.fw_ink))
            if ((kind and InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) isSingleLine = true
        }
        if ((kind and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD) layout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        layout.addView(edit, LinearLayout.LayoutParams(-1, -2))
        container.addView(layout, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(14) })
        return edit
    }
    protected fun message(value: String) { status.text = value; status.visibility = View.VISIBLE }
    protected fun tabs(labels: List<String>, selected: Int = 0, onSelected: (Int) -> Unit): TabLayout = TabLayout(this).apply {
        tabMode = TabLayout.MODE_FIXED
        setBackgroundColor(color(R.color.fw_background))
        setTabTextColors(color(R.color.fw_muted), color(R.color.fw_blue))
        setSelectedTabIndicatorColor(color(R.color.fw_blue))
        labels.forEach { addTab(newTab().setText(it), false) }
        getTabAt(selected.coerceIn(0, labels.lastIndex))?.select()
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(18) }
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { onSelected(tab.position) }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    protected fun <T> request(call: Call<ApiResponse<T>>, onSuccess: (T?) -> Unit) {
        if ((requiresSession && SessionStore.token == null) || (requiresAdmin && !isAdmin) || isFinishing) return
        pendingRequests++; progress.visibility = View.VISIBLE; status.visibility = View.GONE; retry.visibility = View.GONE; calls.add(call)
        fun completed() { calls.remove(call); pendingRequests = (pendingRequests - 1).coerceAtLeast(0); progress.visibility = if (pendingRequests > 0) View.VISIBLE else View.GONE }
        ApiRepository.execute(call, { data ->
            if (!isFinishing && !isDestroyed) { completed(); onSuccess(data) }
        }, { error ->
            if (!isFinishing && !isDestroyed) {
                completed()
                if (error.status == 401 && requiresSession) { SessionStore.clear(); toLogin() }
                else {
                    if (error.status == 401) SessionStore.clear()
                    message(error.localizedMessage(this)); retry.visibility = View.VISIBLE
                    retry.setOnClickListener { request(call.clone(), onSuccess) }
                }
            }
        })
    }
    protected fun logout() { request(RetrofitClient.api.logout()) { SessionStore.clear(); toLogin() } }
    protected fun toLogin() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish()
    }
    override fun onDestroy() { calls.toList().forEach { it.cancel() }; super.onDestroy() }
}

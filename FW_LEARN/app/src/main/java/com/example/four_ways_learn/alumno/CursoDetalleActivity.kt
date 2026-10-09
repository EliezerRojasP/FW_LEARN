package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityCursoDetalleBinding
import com.example.four_ways_learn.model.Leccion
import com.example.four_ways_learn.model.Topico
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class CursoDetalleActivity : BaseActivity() {
    private val expanded = mutableSetOf<Int>()
    private var expansionInitialized = false
    private lateinit var content: ActivityCursoDetalleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.course_details_title))
        content = ActivityCursoDetalleBinding.inflate(layoutInflater, panel, true)
        savedInstanceState?.getIntArray("expanded_topics")
            ?.let { expanded.addAll(it.toList()); expansionInitialized = true }
        val name = intent.getStringExtra("cursoNombre") ?: getString(R.string.your_course)
        content.courseName.text = name; content.heroIcon.setImageResource(courseIcon(name))
        content.courseDescription.text = intent.getStringExtra("cursoDescripcion").orEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray("expanded_topics", expanded.toIntArray()); super.onSaveInstanceState(
            outState
        )
    }

    override fun onResume() {
        super.onResume(); load()
    }

    private fun load() {
        val id = intent.getIntExtra("cursoId", -1)
        if (id < 1) {
            message(getString(R.string.error_course_missing)); return
        }
        request(RetrofitClient.api.topicos(id)) { topics ->
            content.topicList.removeAllViews(); bottomActions()
            val total = topics.orEmpty().sumOf { it.total_lecciones }
            val completed = topics.orEmpty().sumOf { it.lecciones_completadas }
            val value = if (total > 0) completed * 100.0 / total else 0.0
            content.completedCount.text =
                getString(R.string.completed_lessons_count, completed, total)
            content.totalPercent.text = percent(value); updateProgress(content.totalProgress, value)
            if (topics.isNullOrEmpty()) {
                message(getString(R.string.empty_topics)); return@request
            }
            if (!expansionInitialized) {
                expanded.add((topics.firstOrNull { it.total_lecciones > it.lecciones_completadas }
                    ?: topics.first()).id)
                expansionInitialized = true
            }
            topics.forEach { topicCard(it) }
            bottomActions(button(getString(R.string.action_continue_course)) {
                val next = topics.firstOrNull { it.total_lecciones > it.lecciones_completadas }
                    ?: topics.firstOrNull { it.total_lecciones > 0 }
                if (next == null) {
                    message(getString(R.string.empty_lessons)); return@button
                }
                request(RetrofitClient.api.lecciones(next.id)) { lessons ->
                    val lesson =
                        lessons?.firstOrNull { it.completada != 1 } ?: lessons?.firstOrNull()
                    if (lesson == null) message(getString(R.string.empty_lessons)) else openLesson(
                        lesson
                    )
                }
            })
        }
    }

    private fun topicCard(topic: Topico) {
        box(content.topicList, padding = 14) { inside ->
            val title = secondary(topic.nombre) {}.apply {
                setIconResource(if (topic.id in expanded) R.drawable.ic_fw_down else R.drawable.ic_fw_arrow)
                textSize = 15f; gravity =
                android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
                contentDescription = getString(R.string.topic_toggle_accessibility, topic.nombre)
            }
            inside.addView(title)
            inside.addView(
                muted(
                    getString(
                        R.string.lessons_count_percent,
                        topic.lecciones_completadas,
                        topic.total_lecciones,
                        percent(topic.porcentaje_avance)
                    ), 12
                )
            )
            inside.addView(progressBar(topic.porcentaje_avance))
            val rows = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL; visibility = View.GONE
            }
            inside.addView(rows)
            var loaded = false
            var loading = false
            fun populate() {
                rows.visibility = View.VISIBLE
                if (loaded || loading) return
                loading = true
                request(RetrofitClient.api.lecciones(topic.id)) { lessons ->
                    loading = false; loaded = true; rows.removeAllViews()
                    if (lessons.isNullOrEmpty()) rows.addView(muted(getString(R.string.empty_lessons)))
                    lessons.orEmpty()
                        .forEach { lesson -> lessonRow(lesson, rows) { openLesson(lesson) } }
                    rows.visibility = if (topic.id in expanded) View.VISIBLE else View.GONE
                }
            }
            title.setOnClickListener {
                if (topic.id in expanded) {
                    expanded.remove(topic.id); rows.visibility =
                        View.GONE; title.setIconResource(R.drawable.ic_fw_arrow)
                } else {
                    expanded.add(topic.id); title.setIconResource(R.drawable.ic_fw_down); populate()
                }
            }
            if (topic.id in expanded) populate()
        }
    }

    private fun openLesson(lesson: Leccion) {
        startActivity(
            Intent(this, AprendizajeActivity::class.java).putExtra(
                "leccionId",
                lesson.id_leccion
            )
        )
    }
}

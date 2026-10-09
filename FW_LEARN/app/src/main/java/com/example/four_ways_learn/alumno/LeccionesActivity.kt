package com.example.four_ways_learn.alumno

import com.example.four_ways_learn.R
import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class LeccionesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.module_lessons))
    }

    override fun onResume() {
        super.onResume(); load()
    }

    private fun load() {
        val id = intent.getIntExtra("topicoId", -1)
        if (id < 1) {
            message(getString(R.string.error_topic_missing)); return
        }
        request(RetrofitClient.api.lecciones(id)) { lessons ->
            panel.removeAllViews()
            if (lessons.isNullOrEmpty()) {
                message(getString(R.string.empty_lessons)); return@request
            }
            val total = lessons.size;
            val completed = lessons.count { it.completada == 1 }
            progressSummary(
                intent.getStringExtra("topicoNombre") ?: getString(R.string.your_topic),
                completed,
                total,
                completed * 100.0 / total
            )
            panel.addView(text(getString(R.string.your_lessons_title), 22, true))
            lessons.forEach { lesson ->
                val state = when {
                    lesson.completada == 1 -> getString(R.string.state_completed); lesson.total_preguntas == 0 -> getString(
                        R.string.state_content_available
                    ); else -> getString(R.string.state_pending)
                }
                val detail = getString(
                    R.string.lesson_questions_detail,
                    lesson.total_preguntas,
                    getString(if (lesson.completada == 1) R.string.review_available else R.string.read_and_practice)
                )
                val caption = if (lesson.completada == 1) getString(
                    R.string.finished_best_grade,
                    percent(lesson.mejor_puntaje)
                ) else getString(R.string.lesson_progress_title)
                progressCard(
                    getString(R.string.numbered_lesson, lesson.orden, lesson.titulo),
                    detail,
                    lesson.porcentaje_avance,
                    caption,
                    state
                ) {
                    startActivity(
                        Intent(
                            this,
                            AprendizajeActivity::class.java
                        ).putExtra("leccionId", lesson.id_leccion)
                    )
                }
            }
        }
    }
}

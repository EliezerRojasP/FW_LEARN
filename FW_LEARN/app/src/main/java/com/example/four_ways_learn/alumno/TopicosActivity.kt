package com.example.four_ways_learn.alumno

import com.example.four_ways_learn.R
import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class TopicosActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.course_topics_title))
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
            panel.removeAllViews()
            if (topics.isNullOrEmpty()) {
                message(getString(R.string.empty_topics)); return@request
            }
            val total = topics.sumOf { it.total_lecciones };
            val completed = topics.sumOf { it.lecciones_completadas }
            progressSummary(
                intent.getStringExtra("cursoNombre") ?: getString(R.string.your_course),
                completed,
                total,
                if (total > 0) completed * 100.0 / total else 0.0
            )
            panel.addView(text(getString(R.string.choose_topic), 22, true))
            topics.forEach { topic ->
                progressCard(
                    topic.nombre,
                    topic.descripcion.orEmpty(),
                    topic.porcentaje_avance,
                    getString(
                        R.string.lessons_count,
                        topic.lecciones_completadas,
                        topic.total_lecciones
                    ),
                    progressState(topic.lecciones_completadas, topic.total_lecciones)
                ) {
                    startActivity(
                        Intent(this, LeccionesActivity::class.java).putExtra(
                            "topicoId",
                            topic.id
                        ).putExtra("topicoNombre", topic.nombre)
                    )
                }
            }
        }
    }
}

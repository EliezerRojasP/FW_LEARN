package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityProgresoBinding
import com.example.four_ways_learn.network.ResultadoHistorial
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class ProgresoActivity : BaseActivity() {
    private var showingHistory = false
    private var generation = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.progress_title), false)
        showingHistory = savedInstanceState?.getBoolean("showing_history") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("showing_history", showingHistory); super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume(); if (showingHistory) history() else load()
    }

    private fun load() {
        showingHistory = false
        val current = ++generation
        request(RetrofitClient.api.avance()) { data ->
            if (current != generation || data == null) return@request
            panel.removeAllViews()
            val form = ActivityProgresoBinding.inflate(layoutInflater, panel, true)
            form.overallPercent.text = percent(data.porcentaje_avance)
            form.overallCount.text = getString(
                R.string.completed_lessons_count,
                data.lecciones_completadas,
                data.total_lecciones
            )
            data.cursos.forEach { course ->
                progressCard(
                    course.nombre,
                    "",
                    course.porcentaje_avance,
                    getString(
                        R.string.completed_lessons_count,
                        course.lecciones_completadas,
                        course.total_lecciones
                    ),
                    progressState(course.lecciones_completadas, course.total_lecciones),
                    form.courseContainer,
                    courseIcon(course.nombre)
                ) {
                    startActivity(
                        Intent(this, CursoDetalleActivity::class.java).putExtra(
                            "cursoId",
                            course.id
                        )
                            .putExtra("cursoNombre", course.nombre)
                            .putExtra("cursoDescripcion", course.descripcion)
                    )
                }
            }
            data.temas.forEach { topic ->
                progressCard(
                    topic.nombre,
                    "",
                    topic.porcentaje_avance,
                    getString(
                        R.string.completed_lessons_count,
                        topic.lecciones_completadas,
                        topic.total_lecciones
                    ),
                    progressState(topic.lecciones_completadas, topic.total_lecciones),
                    form.topicContainer
                ) {
                    startActivity(
                        Intent(this, LeccionesActivity::class.java).putExtra(
                            "topicoId",
                            topic.id
                        ).putExtra("topicoNombre", topic.nombre)
                    )
                }
            }
            form.historyButton.setOnClickListener { history() }
            request(RetrofitClient.api.resultados()) { items ->
                if (current == generation) {
                    form.latestContainer.removeAllViews()
                    items?.firstOrNull()?.let { resultCard(it, form.latestContainer, true) }
                }
            }
        }
    }

    private fun resultCard(
        result: ResultadoHistorial,
        container: LinearLayout,
        latest: Boolean = false
    ) {
        box(container) { inside ->
            inside.addView(
                muted(
                    getString(if (latest) R.string.last_evaluation else R.string.evaluation_result_title),
                    14
                )
            )
            inside.addView(text(result.titulo, 20, true))
            inside.addView(badge(getString(R.string.lesson_completed)))
            for ((label, value) in listOf(
                getString(R.string.completion_label) to 100.0,
                getString(R.string.grade_label) to result.porcentaje
            )) {
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                row.addView(text(label, 15), LinearLayout.LayoutParams(0, -2, 1f))
                row.addView(text(percent(value), 18, true))
                inside.addView(row)
            }
            inside.addView(
                muted(
                    getString(
                        R.string.history_grade,
                        result.puntaje.toInt(),
                        result.total_preguntas,
                        percent(result.porcentaje),
                        result.fecha_resultado
                    ), 12
                )
            )
            inside.addView(muted(getString(R.string.results_hint), 12))
            val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            if (latest) actions.addView(
                outlined(getString(R.string.action_view_history)) { history() },
                LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(8) })
            if (result.id_leccion > 0) actions.addView(button(getString(R.string.action_retry_short)) {
                startActivity(
                    Intent(this, AprendizajeActivity::class.java).putExtra(
                        "leccionId",
                        result.id_leccion
                    )
                )
            }, LinearLayout.LayoutParams(0, -2, 1f))
            inside.addView(actions)
        }
    }

    private fun history() {
        showingHistory = true
        val current = ++generation
        request(RetrofitClient.api.resultados()) { items ->
            if (current != generation) return@request
            panel.removeAllViews(); panel.addView(
            text(
                getString(R.string.grade_history_title),
                27,
                true
            )
        )
            if (items.isNullOrEmpty()) panel.addView(muted(getString(R.string.empty_grade_history)))
            items.orEmpty().forEach { resultCard(it, panel) }
            panel.addView(outlined(getString(R.string.action_back_progress)) { load() })
            shell.contentScroll.post { shell.contentScroll.scrollTo(0, 0) }
        }
    }
}

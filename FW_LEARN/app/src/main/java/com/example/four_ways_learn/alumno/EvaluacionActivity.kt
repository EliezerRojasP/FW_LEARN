package com.example.four_ways_learn.alumno

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityEvaluacionBinding
import com.example.four_ways_learn.network.ResultadoEvaluacion
import com.example.four_ways_learn.network.SessionStore
import com.example.four_ways_learn.ui.BaseActivity
import com.google.android.material.radiobutton.MaterialRadioButton

class EvaluacionActivity : BaseActivity() {
    private lateinit var vm: EvaluacionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.evaluation_title))
        val id = intent.getIntExtra("leccionId", -1)
        if (id < 1) {
            message(getString(R.string.error_lesson_missing)); return
        }
        vm = ViewModelProvider(this)[EvaluacionViewModel::class.java]
        vm.ui.observe(this) { state ->
            progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            retry.visibility = View.GONE; status.visibility = View.GONE
            if (state.error?.status == 401) {
                SessionStore.clear(); toLogin(); return@observe
            }
            state.error?.let { error ->
                message(error.localizedMessage(this)); retry.visibility =
                View.VISIBLE; retry.setOnClickListener { vm.retry(id) }
            }
            panel.removeAllViews(); bottomActions()
            state.resultado?.let { result -> showResult(result); return@observe }
            val attempt = state.intento ?: return@observe

            val question = attempt.preguntas[state.index]
            val form = ActivityEvaluacionBinding.inflate(layoutInflater, panel, true)
            val total = attempt.preguntas.size
            form.questionTitle.text = question.enunciado
            form.questionPosition.text =
                getString(R.string.question_position, state.index + 1, total)
            updateProgress(form.questionProgress, (state.index + 1) * 100.0 / total)
            form.answersCaption.text =
                getString(R.string.answers_selected_count, vm.answeredCount(), total)
            val letters = listOf(
                R.string.option_letter_a,
                R.string.option_letter_b,
                R.string.option_letter_c,
                R.string.option_letter_d,
                R.string.option_letter_e,
                R.string.option_letter_f
            )
            val group = form.answerGroup
            question.alternativas.forEachIndexed { index, option ->
                val radio = MaterialRadioButton(this).apply {
                    this.id = View.generateViewId(); tag = option.id
                    text = getString(R.string.quiz_option, getString(letters[index]), option.texto)
                    textSize = 16f; minHeight = dp(68); setTextColor(color(R.color.fw_ink))
                    buttonTintList = ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                        intArrayOf(color(R.color.fw_blue), color(R.color.fw_muted))
                    )
                    setPadding(dp(12), dp(14), dp(12), dp(14)); isEnabled = !state.loading
                    setBackgroundResource(R.drawable.bg_answer)
                }
                group.addView(
                    radio,
                    RadioGroup.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
                if (vm.selected(question.id) == option.id) group.check(radio.id)
            }
            group.setOnCheckedChangeListener { _, checked ->
                if (checked != -1) {
                    val radio = group.findViewById<MaterialRadioButton>(checked)
                    vm.choose(question.id, radio.tag as Int)
                    form.answersCaption.text =
                        getString(R.string.answers_selected_count, vm.answeredCount(), total)
                }
            }
            bottomActions(
                secondary(getString(R.string.action_previous_short)) { vm.previous() }.apply {
                    isEnabled = !state.loading && state.index > 0
                },
                button(getString(if (state.index == attempt.preguntas.lastIndex) R.string.action_finish_lesson else R.string.action_next_short)) { vm.next() }.apply {
                    isEnabled = !state.loading
                }
            )
            shell.contentScroll.post { shell.contentScroll.scrollTo(0, 0) }
        }
        vm.load(id)
    }

    private fun showResult(result: ResultadoEvaluacion) {
        panel.addView(badge(getString(R.string.lesson_completed)))
        panel.addView(
            text(
                intent.getStringExtra("leccionTitulo") ?: getString(R.string.your_lesson), 27, true
            )
        )
        box { inside ->
            inside.addView(text(getString(R.string.evaluation_result_title), 20, true))
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for ((label, value) in listOf(
                getString(R.string.completion_label) to result.porcentaje_finalizacion,
                getString(R.string.grade_label) to result.porcentaje
            )) {
                val column = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                column.addView(muted(label, 13)); column.addView(
                    text(
                        percent(value),
                        30,
                        true
                    ).apply { setTextColor(color(R.color.fw_green)) })
                row.addView(column, LinearLayout.LayoutParams(0, -2, 1f))
            }
            inside.addView(row); inside.addView(progressBar(result.porcentaje_finalizacion))
            inside.addView(
                muted(
                    getString(
                        R.string.evaluation_grade,
                        result.puntaje.toInt(),
                        result.total_preguntas,
                        percent(result.porcentaje)
                    ), 14
                )
            )
            inside.addView(muted(getString(R.string.results_hint), 13))
        }
        panel.addView(muted(getString(if (result.aprobada) R.string.evaluation_passed else R.string.evaluation_review_hint)))
        result.tema?.let { topic ->
            progressCard(
                topic.nombre,
                getString(R.string.topic_progress_title),
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

        val backToTopic = outlined(getString(R.string.action_back_topic)) {
            val topicId = result.tema?.id ?: intent.getIntExtra("topicoId", -1)
            if (topicId > 0) startActivity(
                Intent(this, LeccionesActivity::class.java).putExtra("topicoId", topicId)
                    .putExtra("topicoNombre", result.tema?.nombre)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        if (isAdmin) bottomActions(backToTopic)
        else bottomActions(
            backToTopic,
            button(getString(R.string.action_view_progress)) { navigate(ProgresoActivity::class.java) })
        panel.addView(text(getString(R.string.review_answers_title), 22, true))
        result.revision.forEach { review ->
            box { inside ->
                inside.addView(
                    text(
                        getString(
                            R.string.review_question,
                            getString(if (review.correcta) R.string.correct_symbol else R.string.incorrect_symbol),
                            review.enunciado
                        ), 16, true
                    ).apply { setTextColor(color(if (review.correcta) R.color.fw_green else R.color.fw_danger)) })
                inside.addView(
                    muted(
                        getString(R.string.correct_answer, review.respuesta_correcta),
                        14
                    )
                )
            }
        }
        shell.contentScroll.post { shell.contentScroll.scrollTo(0, 0) }
    }
}

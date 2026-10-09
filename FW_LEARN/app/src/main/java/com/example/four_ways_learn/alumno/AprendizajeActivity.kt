package com.example.four_ways_learn.alumno

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityAprendizajeBinding
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import com.example.four_ways_learn.ui.LessonSections
import com.google.android.material.tabs.TabLayout

class AprendizajeActivity : BaseActivity() {
    private var selectedTab = 0
    private var explanationVisible = false
    private lateinit var content: ActivityAprendizajeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.lesson_screen_title))
        content = ActivityAprendizajeBinding.inflate(layoutInflater, panel, true)
        selectedTab = savedInstanceState?.getInt("lesson_tab") ?: 0
        explanationVisible = savedInstanceState?.getBoolean("explanation_visible") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("lesson_tab", selectedTab); outState.putBoolean(
            "explanation_visible",
            explanationVisible
        )
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume(); load()
    }

    private fun load() {
        val id = intent.getIntExtra("leccionId", -1)
        if (id < 1) {
            message(getString(R.string.error_lesson_missing)); return
        }
        request(RetrofitClient.api.leccion(id)) { lesson ->
            if (lesson == null) return@request
            content.lessonTitle.text = lesson.titulo
            content.completionState.text = if (lesson.completada == 1)
                getString(
                    R.string.lesson_finished_best_grade,
                    percent(lesson.mejor_puntaje)
                ) else getString(R.string.lesson_pending)
            content.completionState.setTextColor(color(if (lesson.completada == 1) R.color.fw_green else R.color.fw_muted))
            val parts = LessonSections.parse(lesson.contenido.orEmpty())
            val labels = listOf(R.string.tab_content, R.string.tab_example, R.string.tab_practice)
            fun render() {
                val body = content.lessonBody; body.removeAllViews()
                val value = when (selectedTab) {
                    1 -> parts.example; 2 -> parts.practice; else -> parts.content
                }
                body.addView(text(getString(labels[selectedTab]), 21, true))
                if (selectedTab == 1 && value.isNotBlank()) {
                    val blocks = value.split("\n\n", limit = 2)
                    body.addView(muted(getString(R.string.section_code), 12))
                    body.addView(text(blocks.first(), 15).apply {
                        typeface = Typeface.MONOSPACE; setTextIsSelectable(true)
                        setTextColor(color(R.color.fw_on_navy)); setBackgroundResource(R.drawable.bg_code)
                        setPadding(dp(16), dp(18), dp(16), dp(18))
                    })
                    if (blocks.size > 1) body.addView(
                        text(
                            blocks[1],
                            16
                        ).apply { setTextIsSelectable(true) })
                } else body.addView(
                    text(
                        value.ifBlank { getString(R.string.section_missing) },
                        16
                    ).apply { setTextIsSelectable(true) })
                if (selectedTab == 0 && parts.example.isNotBlank()) {
                    body.addView(text(getString(R.string.tab_example), 18, true))
                    body.addView(text(parts.example.substringBefore("\n\n"), 15).apply {
                        typeface = Typeface.MONOSPACE; setTextIsSelectable(true)
                        setTextColor(color(R.color.fw_on_navy)); setBackgroundResource(R.drawable.bg_code)
                        setPadding(dp(16), dp(18), dp(16), dp(18))
                    })
                }
                if (selectedTab == 2 && parts.explanation.isNotBlank()) {
                    body.addView(muted(getString(R.string.practice_hint), 13))
                    val explanation = text(parts.explanation, 16).apply {
                        setTextIsSelectable(true); visibility =
                        if (explanationVisible) View.VISIBLE else View.GONE
                    }
                    val toggle =
                        secondary(getString(if (explanationVisible) R.string.action_hide_explanation else R.string.action_show_explanation)) {}
                    toggle.setOnClickListener {
                        explanationVisible = !explanationVisible
                        explanation.visibility = if (explanationVisible) View.VISIBLE else View.GONE
                        toggle.setText(if (explanationVisible) R.string.action_hide_explanation else R.string.action_show_explanation)
                    }
                    body.addView(toggle); body.addView(explanation)
                }
            }
            content.lessonTabs.clearOnTabSelectedListeners(); content.lessonTabs.removeAllTabs()
            labels.forEach {
                content.lessonTabs.addTab(
                    content.lessonTabs.newTab().setText(it),
                    false
                )
            }
            selectedTab = selectedTab.coerceIn(0, 2)
            content.lessonTabs.getTabAt(selectedTab)?.select()
            content.lessonTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedTab = tab.position; render()
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            render()
            bottomActions(button(getString(if (lesson.completada == 1) R.string.action_practice_again else R.string.action_start_evaluation)) {
                startActivity(
                    Intent(this, EvaluacionActivity::class.java).putExtra(
                        "leccionId",
                        lesson.id_leccion
                    )
                        .putExtra("leccionTitulo", lesson.titulo)
                        .putExtra("topicoId", lesson.id_topico)
                )
            }.apply { isEnabled = lesson.total_preguntas > 0 })
            if (lesson.total_preguntas == 0) message(getString(R.string.evaluation_unavailable))
        }
    }
}

package com.example.four_ways_learn.admin

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityAdminBinding
import com.example.four_ways_learn.databinding.ItemStatCardBinding
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class AdminActivity : BaseActivity() {
    override val requiresAdmin = true
    private lateinit var content: ActivityAdminBinding
    private lateinit var courseStat: ItemStatCardBinding
    private lateinit var lessonStat: ItemStatCardBinding
    private lateinit var questionStat: ItemStatCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screen(getString(R.string.app_title), false)
        content = ActivityAdminBinding.inflate(layoutInflater, panel, true)
        fun stat(label: Int, icon: Int): ItemStatCardBinding {
            val card = ItemStatCardBinding.inflate(layoutInflater, content.statsRow, false)
            card.statLabel.setText(label); card.statIcon.setImageResource(icon)
            card.statNumber.text = getString(R.string.stat_placeholder)
            content.statsRow.addView(
                card.root,
                LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(6) })
            return card
        }
        courseStat = stat(R.string.module_courses, R.drawable.ic_fw_book)
        lessonStat = stat(R.string.module_lessons, R.drawable.ic_fw_lesson)
        questionStat = stat(R.string.short_questions, R.drawable.ic_fw_question)
        val modules = listOf(
            Triple(content.tileUsers, "usuarios", R.string.module_users),
            Triple(content.tileCourses, "cursos", R.string.module_courses),
            Triple(content.tileTopics, "topicos", R.string.module_topics),
            Triple(content.tileLessons, "lecciones", R.string.module_lessons),
            Triple(content.tileQuestions, "preguntas", R.string.short_questions),
            Triple(content.tileResults, "resultados", R.string.nav_results),
            Triple(content.statisticsCard, "estadisticas", R.string.module_stats)
        )
        modules.forEach { (view, resource, label) ->
            view.setOnClickListener {
                startActivity(
                    Intent(this, AdminListActivity::class.java)
                        .putExtra("recurso", resource).putExtra("titulo", getString(label))
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        request(RetrofitClient.api.adminList("estadisticas")) { data ->
            val stats = data?.takeIf { it.isJsonObject }?.asJsonObject ?: return@request
            courseStat.statNumber.text = stats.get("cursos")?.asString.orEmpty()
            lessonStat.statNumber.text = stats.get("lecciones")?.asString.orEmpty()
        }
        request(RetrofitClient.api.adminList("preguntas")) { data ->
            val count =
                data?.takeIf { it.isJsonArray }?.asJsonArray?.count { it.asJsonObject.get("estado")?.asInt == 1 }
                    ?: 0
            questionStat.statNumber.text = getString(R.string.stat_integer, count)
        }
    }
}

package com.example.four_ways_learn.admin

import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityAdminContentBinding
import com.example.four_ways_learn.ui.BaseActivity

class AdminContentActivity : BaseActivity() {
    override val requiresAdmin = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.nav_content), false)
        val content = ActivityAdminContentBinding.inflate(layoutInflater, panel, true)
        listOf(
            Triple(content.tileCourses, "cursos", R.string.module_courses),
            Triple(content.tileTopics, "topicos", R.string.module_topics),
            Triple(content.tileLessons, "lecciones", R.string.module_lessons),
            Triple(content.tileQuestions, "preguntas", R.string.short_questions)
        ).forEach { (view, resource, label) ->
            view.setOnClickListener {
                startActivity(
                    Intent(this, AdminListActivity::class.java)
                        .putExtra("recurso", resource).putExtra("titulo", getString(label))
                )
            }
        }
    }
}

package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityCursosBinding
import com.example.four_ways_learn.databinding.FeaturedLearningBinding
import com.example.four_ways_learn.model.Curso
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.network.SessionStore
import com.example.four_ways_learn.ui.BaseActivity

open class CursosActivity : BaseActivity() {
    private lateinit var content: ActivityCursosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screen(getString(R.string.app_title), false)
        content = ActivityCursosBinding.inflate(layoutInflater, panel, true)
        content.greeting.text = getString(
            R.string.dashboard_greeting,
            SessionStore.usuario?.nombre ?: getString(R.string.role_student)
        )
        content.subtitle.setText(R.string.courses_title)
    }

    override fun onResume() {
        super.onResume(); load()
    }

    private fun load() {
        request(RetrofitClient.api.cursos()) { courses ->
            content.featuredContainer.removeAllViews(); content.courseList.removeAllViews()
            if (courses.isNullOrEmpty()) {
                message(getString(R.string.empty_courses)); return@request
            }
            content.allCoursesButton.text = getString(R.string.catalog_count, courses.size)
            content.allCoursesButton.setOnClickListener {
                shell.contentScroll.smoothScrollTo(0, content.root.top + content.courseList.top)
            }
            val total = courses.sumOf { it.total_lecciones }
            val completed = courses.sumOf { it.lecciones_completadas }
            val value = if (total > 0) 100.0 * completed / total else 0.0
            val next = courses.firstOrNull { it.total_lecciones > it.lecciones_completadas }
                ?: courses.first()
            val featured =
                FeaturedLearningBinding.inflate(layoutInflater, content.featuredContainer, true)
            featured.featuredTitle.text = next.nombre
            featured.featuredCourse.setText(R.string.featured_empty)
            featured.featuredIcon.setImageResource(courseIcon(next.nombre))
            featured.featuredCount.text =
                getString(R.string.lessons_count_percent, completed, total, percent(value))
            updateProgress(featured.featuredProgress, value)
            featured.continueButton.setOnClickListener { openCourse(next) }
            request(RetrofitClient.api.topicos(next.id)) topicsRequest@{ topics ->
                val topic = topics?.firstOrNull { it.total_lecciones > it.lecciones_completadas }
                    ?: topics?.firstOrNull { it.total_lecciones > 0 } ?: return@topicsRequest
                request(RetrofitClient.api.lecciones(topic.id)) lessonsRequest@{ lessons ->
                    val lesson =
                        lessons?.firstOrNull { it.completada != 1 } ?: lessons?.firstOrNull()
                        ?: return@lessonsRequest
                    featured.featuredTitle.text = lesson.titulo
                    featured.featuredCourse.text = next.nombre
                    featured.continueButton.setOnClickListener {
                        startActivity(
                            Intent(
                                this,
                                AprendizajeActivity::class.java
                            ).putExtra("leccionId", lesson.id_leccion)
                        )
                    }
                }
            }
            courses.forEach { course ->
                progressCard(
                    course.nombre,
                    course.descripcion,
                    course.porcentaje_avance,
                    getString(
                        R.string.completed_lessons_count,
                        course.lecciones_completadas,
                        course.total_lecciones
                    ),
                    progressState(course.lecciones_completadas, course.total_lecciones),
                    content.courseList,
                    courseIcon(course.nombre)
                ) { openCourse(course) }
            }
        }
    }

    private fun openCourse(course: Curso) {
        startActivity(
            Intent(this, CursoDetalleActivity::class.java).putExtra("cursoId", course.id)
                .putExtra("cursoNombre", course.nombre)
                .putExtra("cursoDescripcion", course.descripcion)
        )
    }
}

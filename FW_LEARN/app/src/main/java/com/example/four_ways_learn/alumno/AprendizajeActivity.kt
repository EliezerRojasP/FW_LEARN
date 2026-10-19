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
    private var pestanaSeleccionada = 0
    private var explicacionVisible = false
    private lateinit var content: ActivityAprendizajeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.titulo_pantalla_leccion))
        content = ActivityAprendizajeBinding.inflate(layoutInflater, panel, true)
        pestanaSeleccionada = savedInstanceState?.getInt("lesson_tab") ?: 0
        explicacionVisible = savedInstanceState?.getBoolean("explanation_visible") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("lesson_tab", pestanaSeleccionada); outState.putBoolean(
            "explanation_visible",
            explicacionVisible
        )
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume(); cargar()
    }

    private fun cargar() {
        val id = intent.getIntExtra("leccionId", -1)
        if (id < 1) {
            mensaje(getString(R.string.error_leccion_faltante)); return
        }
        peticion(RetrofitClient.api.leccion(id)) { leccion ->
            if (leccion == null) return@peticion
            content.tituloLeccion.text = leccion.titulo
            content.estadoFinalizacion.text = if (leccion.completada == 1)
                getString(
                    R.string.leccion_finalizada_mejor_nota,
                    porcentajeTexto(leccion.mejor_puntaje)
                ) else getString(R.string.leccion_pendiente)
            content.estadoFinalizacion.setTextColor(color(if (leccion.completada == 1) R.color.fw_verde else R.color.fw_apagado))
            val partes = LessonSections.parse(leccion.contenido.orEmpty())
            val etiquetas = listOf(R.string.pestana_contenido, R.string.pestana_ejemplo, R.string.pestana_practica)
            fun renderizar() {
                val body = content.cuerpoLeccion; body.removeAllViews()
                val valor = when (pestanaSeleccionada) {
                    1 -> partes.example; 2 -> partes.practice; else -> partes.content
                }
                body.addView(text(getString(etiquetas[pestanaSeleccionada]), 21, true))
                if (pestanaSeleccionada == 1 && valor.isNotBlank()) {
                    val bloques = valor.split("\n\n", limit = 2)
                    body.addView(atenuado(getString(R.string.seccion_codigo), 12))
                    body.addView(text(bloques.first(), 15).apply {
                        typeface = Typeface.MONOSPACE; setTextIsSelectable(true)
                        setTextColor(color(R.color.fw_sobre_marino)); setBackgroundResource(R.drawable.fondo_codigo)
                        setPadding(dp(16), dp(18), dp(16), dp(18))
                    })
                    if (bloques.size > 1) body.addView(
                        text(
                            bloques[1],
                            16
                        ).apply { setTextIsSelectable(true) })
                } else body.addView(
                    text(
                        valor.ifBlank { getString(R.string.seccion_faltante) },
                        16
                    ).apply { setTextIsSelectable(true) })
                if (pestanaSeleccionada == 0 && partes.example.isNotBlank()) {
                    body.addView(text(getString(R.string.pestana_ejemplo), 18, true))
                    body.addView(text(partes.example.substringBefore("\n\n"), 15).apply {
                        typeface = Typeface.MONOSPACE; setTextIsSelectable(true)
                        setTextColor(color(R.color.fw_sobre_marino)); setBackgroundResource(R.drawable.fondo_codigo)
                        setPadding(dp(16), dp(18), dp(16), dp(18))
                    })
                }
                if (pestanaSeleccionada == 2 && partes.explanation.isNotBlank()) {
                    body.addView(atenuado(getString(R.string.sugerencia_practica), 13))
                    val explanation = text(partes.explanation, 16).apply {
                        setTextIsSelectable(true); visibility =
                        if (explicacionVisible) View.VISIBLE else View.GONE
                    }
                    val alternar =
                        secundario(getString(if (explicacionVisible) R.string.accion_ocultar_explicacion else R.string.accion_mostrar_explicacion)) {}
                    alternar.setOnClickListener {
                        explicacionVisible = !explicacionVisible
                        explanation.visibility = if (explicacionVisible) View.VISIBLE else View.GONE
                        alternar.setText(if (explicacionVisible) R.string.accion_ocultar_explicacion else R.string.accion_mostrar_explicacion)
                    }
                    body.addView(alternar); body.addView(explanation)
                }
            }
            content.pestanasLeccion.clearOnTabSelectedListeners(); content.pestanasLeccion.removeAllTabs()
            etiquetas.forEach {
                content.pestanasLeccion.addTab(
                    content.pestanasLeccion.newTab().setText(it),
                    false
                )
            }
            pestanaSeleccionada = pestanaSeleccionada.coerceIn(0, 2)
            content.pestanasLeccion.getTabAt(pestanaSeleccionada)?.select()
            content.pestanasLeccion.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    pestanaSeleccionada = tab.position; renderizar()
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            renderizar()
            bottomActions(boton(getString(if (leccion.completada == 1) R.string.accion_practicar_de_nuevo else R.string.accion_comenzar_evaluacion)) {
                startActivity(
                    Intent(this, EvaluacionActivity::class.java).putExtra(
                        "leccionId",
                        leccion.id_leccion
                    )
                        .putExtra("leccionTitulo", leccion.titulo)
                        .putExtra("topicoId", leccion.id_topico)
                )
            }.apply { isEnabled = leccion.total_preguntas > 0 })
            if (leccion.total_preguntas == 0) mensaje(getString(R.string.evaluacion_no_disponible))
        }
    }
}

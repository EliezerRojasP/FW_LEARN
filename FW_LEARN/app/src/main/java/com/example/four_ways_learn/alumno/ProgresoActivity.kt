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

    private var mostrandoHistorial = false
    private var generacion = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.titulo_app), false)
        mostrandoHistorial = savedInstanceState?.getBoolean("showing_history") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("showing_history", mostrandoHistorial); super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume(); if (mostrandoHistorial) historial() else cargar()
    }

    private fun cargar() {
        mostrandoHistorial = false
        val actual = ++generacion
        peticion(RetrofitClient.api.avance()) { data ->
            if (actual != generacion || data == null) return@peticion
            panel.removeAllViews()
            val formulario = ActivityProgresoBinding.inflate(layoutInflater, panel, true)
            formulario.porcentajeGeneral.text = porcentajeTexto(data.porcentaje_avance)
            formulario.conteoGeneral.text = getString(
                R.string.conteo_lecciones_completadas,
                data.lecciones_completadas,
                data.total_lecciones
            )
            data.cursos.forEach { curso ->
                tarjetaProgreso(
                    curso.nombre,
                    "",
                    curso.porcentaje_avance,
                    getString(
                        R.string.conteo_lecciones_completadas,
                        curso.lecciones_completadas,
                        curso.total_lecciones
                    ),
                    estadoProgreso(curso.lecciones_completadas, curso.total_lecciones),
                    formulario.contenedorCurso,
                    iconoCurso(curso.nombre)
                ) {
                    startActivity(
                        Intent(this, CursoDetalleActivity::class.java).putExtra(
                            "cursoId",
                            curso.id
                        )
                            .putExtra("cursoNombre", curso.nombre)
                            .putExtra("cursoDescripcion", curso.descripcion)
                    )
                }
            }
            data.temas.forEach { tema ->
                tarjetaProgreso(
                    tema.nombre,
                    "",
                    tema.porcentaje_avance,
                    getString(
                        R.string.conteo_lecciones_completadas,
                        tema.lecciones_completadas,
                        tema.total_lecciones
                    ),
                    estadoProgreso(tema.lecciones_completadas, tema.total_lecciones),
                    formulario.contenedorTemas
                ) {
                    startActivity(
                        Intent(this, LeccionesActivity::class.java).putExtra(
                            "topicoId",
                            tema.id
                        ).putExtra("topicoNombre", tema.nombre)
                    )
                }
            }
            formulario.botonHistorial.setOnClickListener { historial() }
            peticion(RetrofitClient.api.resultados()) { elementos ->
                if (actual == generacion) {
                    formulario.contenedorReciente.removeAllViews()
                    elementos?.firstOrNull()?.let { tarjetaResultado(it, formulario.contenedorReciente, true) }
                }
            }
        }
    }

    private fun tarjetaResultado(
        resultado: ResultadoHistorial,
        contenedor: LinearLayout,
        masReciente: Boolean = false
    ) {
        caja(contenedor) { inside ->
            inside.addView(
                atenuado(
                    getString(if (masReciente) R.string.ultima_evaluacion else R.string.titulo_resultado_evaluacion),
                    14
                )
            )
            inside.addView(text(resultado.titulo, 20, true))
            inside.addView(insignia(getString(R.string.leccion_completada)))
            for ((etiqueta, valor) in listOf(
                getString(R.string.etiqueta_finalizacion) to 100.0,
                getString(R.string.etiqueta_nota) to resultado.porcentaje
            )) {
                val fila = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                fila.addView(text(etiqueta, 15), LinearLayout.LayoutParams(0, -2, 1f))
                fila.addView(text(porcentajeTexto(valor), 18, true))
                inside.addView(fila)
            }
            inside.addView(
                atenuado(
                    getString(
                        R.string.nota_historial,
                        resultado.puntaje.toInt(),
                        resultado.total_preguntas,
                        porcentajeTexto(resultado.porcentaje),
                        resultado.fecha_resultado
                    ), 12
                )
            )
            inside.addView(atenuado(getString(R.string.sugerencia_resultados), 12))
            val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            if (masReciente) actions.addView(
                contorno(getString(R.string.accion_ver_historial)) { historial() },
                LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(8) })
            if (resultado.id_leccion > 0) actions.addView(boton(getString(R.string.accion_reintentar_corto)) {
                startActivity(
                    Intent(this, AprendizajeActivity::class.java).putExtra(
                        "leccionId",
                        resultado.id_leccion
                    )
                )
            }, LinearLayout.LayoutParams(0, -2, 1f))
            inside.addView(actions)
        }
    }

    private fun historial() {
        mostrandoHistorial = true
        val actual = ++generacion
        peticion(RetrofitClient.api.resultados()) { elementos ->
            if (actual != generacion) return@peticion
            panel.removeAllViews(); panel.addView(
            text(
                getString(R.string.titulo_historial_notas),
                27,
                true
            )
        )
            if (elementos.isNullOrEmpty()) panel.addView(atenuado(getString(R.string.historial_notas_vacio)))
            elementos.orEmpty().forEach { tarjetaResultado(it, panel) }
            panel.addView(contorno(getString(R.string.accion_volver_progreso)) { cargar() })
            marco.scrollContenido.post { marco.scrollContenido.scrollTo(0, 0) }
        }
    }
}

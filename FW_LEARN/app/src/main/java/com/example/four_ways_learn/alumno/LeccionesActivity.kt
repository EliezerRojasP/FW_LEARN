package com.example.four_ways_learn.alumno

import com.example.four_ways_learn.R
import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class LeccionesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.modulo_lecciones))
    }

    override fun onResume() {
        super.onResume(); cargar()
    }

    private fun cargar() {
        val id = intent.getIntExtra("topicoId", -1)
        if (id < 1) {
            mensaje(getString(R.string.error_tema_faltante)); return
        }
        peticion(RetrofitClient.api.lecciones(id)) { lecciones ->
            panel.removeAllViews()
            if (lecciones.isNullOrEmpty()) {
                mensaje(getString(R.string.lecciones_vacias)); return@peticion
            }
            val total = lecciones.size;
            val completadas = lecciones.count { it.completada == 1 }
            resumenProgreso(
                intent.getStringExtra("topicoNombre") ?: getString(R.string.tu_tema),
                completadas,
                total,
                completadas * 100.0 / total
            )
            panel.addView(text(getString(R.string.titulo_tus_lecciones), 22, true))
            lecciones.forEach { leccion ->
                val estadoActual = when {
                    leccion.completada == 1 -> getString(R.string.estado_completada); leccion.total_preguntas == 0 -> getString(
                        R.string.estado_contenido_disponible
                    ); else -> getString(R.string.estado_pendiente)
                }
                val detalle = getString(
                    R.string.detalle_preguntas_leccion,
                    leccion.total_preguntas,
                    getString(if (leccion.completada == 1) R.string.repaso_disponible else R.string.leer_y_practicar)
                )
                val leyenda = if (leccion.completada == 1) getString(
                    R.string.finalizada_mejor_nota,
                    porcentajeTexto(leccion.mejor_puntaje)
                ) else getString(R.string.titulo_progreso_leccion)
                tarjetaProgreso(
                    getString(R.string.leccion_numerada, leccion.orden, leccion.titulo),
                    detalle,
                    leccion.porcentaje_avance,
                    leyenda,
                    estadoActual
                ) {
                    startActivity(
                        Intent(
                            this,
                            AprendizajeActivity::class.java
                        ).putExtra("leccionId", leccion.id_leccion)
                    )
                }
            }
        }
    }
}

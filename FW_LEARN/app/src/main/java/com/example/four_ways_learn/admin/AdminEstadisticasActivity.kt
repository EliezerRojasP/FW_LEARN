package com.example.four_ways_learn.admin

import android.os.Bundle
import com.example.four_ways_learn.R
import com.example.four_ways_learn.model.EstadisticasAdmin
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class AdminEstadisticasActivity : BaseActivity() {
    override val requiereAdmin = true

    private var generacion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.modulo_estadisticas))
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        val actual = ++generacion
        panel.removeAllViews()
        marco.tituloEncabezado.setText(R.string.modulo_estadisticas)
        mensajeEstado.visibility = android.view.View.GONE
        reintentar.visibility = android.view.View.GONE
        bottomActions(boton(getString(R.string.accion_actualizar)) { cargar() })

        peticion(RetrofitClient.api.estadisticasAdmin()) { datos ->
            if (actual != generacion) return@peticion
            dibujar(datos)
        }
    }

    private fun dibujar(datos: EstadisticasAdmin?) {
        val stats = datos ?: return
        panel.addView(text(getString(R.string.modulo_estadisticas), 27, true))
        panel.addView(atenuado(getString(R.string.sugerencia_estadisticas_admin), 14))
        caja { inside ->
            inside.addView(atenuado(getString(R.string.estadistica_alumnos), 14))
            inside.addView(text(getString(R.string.estadistica_entero, stats.alumnos), 30, true))
        }
        caja { inside ->
            inside.addView(atenuado(getString(R.string.estadistica_cursos), 14))
            inside.addView(text(getString(R.string.estadistica_entero, stats.cursos), 30, true))
        }
        caja { inside ->
            inside.addView(atenuado(getString(R.string.estadistica_lecciones), 14))
            inside.addView(text(getString(R.string.estadistica_entero, stats.lecciones), 30, true))
        }
        caja { inside ->
            inside.addView(atenuado(getString(R.string.estadistica_intentos), 14))
            inside.addView(text(getString(R.string.estadistica_entero, stats.intentos), 30, true))
        }
        caja { inside ->
            inside.addView(atenuado(getString(R.string.promedio_estadisticas_admin), 14))
            inside.addView(text(porcentajeTexto(stats.promedio), 30, true))
        }
    }
}

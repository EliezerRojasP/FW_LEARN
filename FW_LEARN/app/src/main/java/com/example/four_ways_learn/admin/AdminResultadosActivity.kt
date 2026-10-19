package com.example.four_ways_learn.admin

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.example.four_ways_learn.R
import com.example.four_ways_learn.model.ResultadoAdmin
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class AdminResultadosActivity : BaseActivity() {
    override val requiereAdmin = true

    private var resultados = listOf<ResultadoAdmin>()
    private var consulta = ""
    private var generacion = 0

    private var vistaConteo: TextView? = null
    private var contenedorLista: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.modulo_resultados))
        consulta = savedInstanceState?.getString("consulta").orEmpty()
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("consulta", consulta)
        super.onSaveInstanceState(outState)
    }

    private fun cargar() {
        val actual = ++generacion
        panel.removeAllViews()
        marco.tituloEncabezado.setText(R.string.modulo_resultados)
        mensajeEstado.visibility = android.view.View.GONE
        reintentar.visibility = android.view.View.GONE

        val busqueda = campoTexto(getString(R.string.buscar_registros), consulta)
        vistaConteo = atenuado("", 12).also { panel.addView(it) }
        contenedorLista = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            .also { panel.addView(it) }
        busqueda.doAfterTextChanged { consulta = it.toString(); dibujar() }
        bottomActions(boton(getString(R.string.accion_actualizar)) { cargar() })

        peticion(RetrofitClient.api.resultadosAdmin()) { lista ->
            if (actual != generacion) return@peticion
            resultados = lista.orEmpty()
            dibujar()
        }
    }

    private fun dibujar() {
        val lista = contenedorLista ?: return
        val conteo = vistaConteo ?: return
        lista.removeAllViews()
        val filtrados = resultados.filter(::coincide)
        conteo.text = getString(R.string.conteo_resultados_filtro, filtrados.size)
        if (filtrados.isEmpty()) {
            lista.addView(
                atenuado(
                    getString(
                        if (resultados.isEmpty()) R.string.registros_vacios
                        else R.string.sin_resultados_busqueda
                    )
                )
            )
        }
        filtrados.forEach { pintarResultado(it, lista) }
    }

    private fun coincide(resultado: ResultadoAdmin): Boolean = consulta.isBlank() ||
        resultado.titulo.contains(consulta, true) ||
        resultado.alumno.contains(consulta, true)

    private fun pintarResultado(resultado: ResultadoAdmin, lista: LinearLayout) {
        caja(lista) { inside ->
            inside.addView(text(resultado.titulo, 18, true))
            inside.addView(atenuado(resultado.alumno, 14))
            inside.addView(insignia(porcentajeTexto(resultado.porcentaje)))
            inside.addView(
                atenuado(
                    getString(
                        R.string.nota_admin,
                        numero(resultado.puntaje),
                        resultado.total_preguntas.toString(),
                        numero(resultado.porcentaje),
                        resultado.fecha_resultado
                    ), 13
                )
            )
        }
    }

    private fun numero(valor: Double): String =
        if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()
}

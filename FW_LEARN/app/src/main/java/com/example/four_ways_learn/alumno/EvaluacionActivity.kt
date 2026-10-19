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
        super.onCreate(savedInstanceState); pantalla(getString(R.string.titulo_evaluacion))
        val id = intent.getIntExtra("leccionId", -1)
        if (id < 1) {
            mensaje(getString(R.string.error_leccion_faltante)); return
        }
        vm = ViewModelProvider(this)[EvaluacionViewModel::class.java]
        vm.interfaz.observe(this) { estadoActual ->
            progreso.visibility = if (estadoActual.cargando) View.VISIBLE else View.GONE
            reintentar.visibility = View.GONE; mensajeEstado.visibility = View.GONE
            if (estadoActual.error?.mensajeEstado == 401) {
                SessionStore.clear(); irALogin(); return@observe
            }
            estadoActual.error?.let { error ->
                mensaje(error.localizedMessage(this)); reintentar.visibility =
                View.VISIBLE; reintentar.setOnClickListener { vm.reintentar(id) }
            }
            panel.removeAllViews(); bottomActions()
            estadoActual.resultado?.let { resultado -> showResult(resultado); return@observe }
            val intento = estadoActual.intento ?: return@observe

            val pregunta = intento.preguntas[estadoActual.indice]
            val formulario = ActivityEvaluacionBinding.inflate(layoutInflater, panel, true)
            val total = intento.preguntas.size
            formulario.tituloPregunta.text = pregunta.enunciado
            formulario.posicionPregunta.text =
                getString(R.string.posicion_pregunta, estadoActual.indice + 1, total)
            actualizarProgreso(formulario.progresoPregunta, (estadoActual.indice + 1) * 100.0 / total)
            formulario.leyendaRespuestas.text =
                getString(R.string.conteo_respuestas_seleccionadas, vm.cantidadRespondidas(), total)
            val letras = listOf(
                R.string.letra_opcion_a,
                R.string.letra_opcion_b,
                R.string.letra_opcion_c,
                R.string.letra_opcion_d,
                R.string.letra_opcion_e,
                R.string.letra_opcion_f
            )
            val grupo = formulario.grupoRespuestas
            pregunta.alternativas.forEachIndexed { indice, alternativa ->
                val botonRadio = MaterialRadioButton(this).apply {
                    this.id = View.generateViewId(); tag = alternativa.id
                    text = getString(R.string.opcion_cuestionario, getString(letras[indice]), alternativa.texto)
                    textSize = 16f; minHeight = dp(68); setTextColor(color(R.color.fw_tinta))
                    buttonTintList = ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                        intArrayOf(color(R.color.fw_azul), color(R.color.fw_apagado))
                    )
                    setPadding(dp(12), dp(14), dp(12), dp(14)); isEnabled = !estadoActual.cargando
                    setBackgroundResource(R.drawable.fondo_respuesta)
                }
                grupo.addView(
                    botonRadio,
                    RadioGroup.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
                if (vm.seleccionada(pregunta.id) == alternativa.id) grupo.check(botonRadio.id)
            }
            grupo.setOnCheckedChangeListener { _, marcado ->
                if (marcado != -1) {
                    val botonRadio = grupo.findViewById<MaterialRadioButton>(marcado)
                    vm.elegir(pregunta.id, botonRadio.tag as Int)
                    formulario.leyendaRespuestas.text =
                        getString(R.string.conteo_respuestas_seleccionadas, vm.cantidadRespondidas(), total)
                }
            }
            bottomActions(
                secundario(getString(R.string.accion_anterior_corto)) { vm.anterior() }.apply {
                    isEnabled = !estadoActual.cargando && estadoActual.indice > 0
                },
                boton(getString(if (estadoActual.indice == intento.preguntas.lastIndex) R.string.accion_finalizar_leccion else R.string.accion_siguiente_corto)) { vm.siguiente() }.apply {
                    isEnabled = !estadoActual.cargando
                }
            )
            marco.scrollContenido.post { marco.scrollContenido.scrollTo(0, 0) }
        }
        vm.cargar(id)
    }

    private fun showResult(resultado: ResultadoEvaluacion) {
        panel.addView(insignia(getString(R.string.leccion_completada)))
        panel.addView(
            text(
                intent.getStringExtra("leccionTitulo") ?: getString(R.string.tu_leccion), 27, true
            )
        )
        caja { inside ->
            inside.addView(text(getString(R.string.titulo_resultado_evaluacion), 20, true))
            val fila = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for ((etiqueta, valor) in listOf(
                getString(R.string.etiqueta_finalizacion) to resultado.porcentaje_finalizacion,
                getString(R.string.etiqueta_nota) to resultado.porcentaje
            )) {
                val columna = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                columna.addView(atenuado(etiqueta, 13)); columna.addView(
                    text(
                        porcentajeTexto(valor),
                        30,
                        true
                    ).apply { setTextColor(color(R.color.fw_verde)) })
                fila.addView(columna, LinearLayout.LayoutParams(0, -2, 1f))
            }
            inside.addView(fila); inside.addView(barraProgreso(resultado.porcentaje_finalizacion))
            inside.addView(
                atenuado(
                    getString(
                        R.string.nota_evaluacion,
                        resultado.puntaje.toInt(),
                        resultado.total_preguntas,
                        porcentajeTexto(resultado.porcentaje)
                    ), 14
                )
            )
            inside.addView(atenuado(getString(R.string.sugerencia_resultados), 13))
        }
        panel.addView(atenuado(getString(if (resultado.aprobada) R.string.evaluacion_aprobada else R.string.sugerencia_revision_evaluacion)))
        resultado.tema?.let { tema ->
            tarjetaProgreso(
                tema.nombre,
                getString(R.string.titulo_progreso_tema),
                tema.porcentaje_avance,
                getString(
                    R.string.conteo_lecciones,
                    tema.lecciones_completadas,
                    tema.total_lecciones
                ),
                estadoProgreso(tema.lecciones_completadas, tema.total_lecciones)
            ) {
                startActivity(
                    Intent(this, LeccionesActivity::class.java).putExtra(
                        "topicoId",
                        tema.id
                    ).putExtra("topicoNombre", tema.nombre)
                )
            }
        }

        val backToTopic = contorno(getString(R.string.accion_volver_tema)) {
            val topicId = resultado.tema?.id ?: intent.getIntExtra("topicoId", -1)
            if (topicId > 0) startActivity(
                Intent(this, LeccionesActivity::class.java).putExtra("topicoId", topicId)
                    .putExtra("topicoNombre", resultado.tema?.nombre)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        if (esAdmin) bottomActions(backToTopic)
        else bottomActions(
            backToTopic,
            boton(getString(R.string.accion_ver_progreso)) { navegar(ProgresoActivity::class.java) })
        panel.addView(text(getString(R.string.titulo_revisar_respuestas), 22, true))
        resultado.revision.forEach { review ->
            caja { inside ->
                inside.addView(
                    text(
                        getString(
                            R.string.pregunta_revision,
                            getString(if (review.correcta) R.string.simbolo_correcto else R.string.simbolo_incorrecto),
                            review.enunciado
                        ), 16, true
                    ).apply { setTextColor(color(if (review.correcta) R.color.fw_verde else R.color.fw_peligro)) })
                inside.addView(
                    atenuado(
                        getString(R.string.respuesta_correcta, review.respuesta_correcta),
                        14
                    )
                )
            }
        }
        marco.scrollContenido.post { marco.scrollContenido.scrollTo(0, 0) }
    }
}

package com.example.four_ways_learn.admin

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.FormAdminPreguntaBinding
import com.example.four_ways_learn.databinding.ItemAdminRecordBinding
import com.example.four_ways_learn.model.AlternativaAdmin
import com.example.four_ways_learn.model.IdPregunta
import com.example.four_ways_learn.model.LeccionAdmin
import com.example.four_ways_learn.model.PreguntaAdmin
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AdminPreguntasActivity : BaseActivity() {
    override val requiereAdmin = true

    private class Borrador(
        val preguntaId: Int?,
        val leccionId: Int?,
        val enunciado: String,
        val orden: String,
        val activo: Boolean,
        val alternativas: List<String>,
        val correcta: Int
    )

    private var lecciones = listOf<LeccionAdmin>()
    private var preguntas = listOf<PreguntaAdmin>()
    private var consulta = ""
    private var filtro = 0
    private var generacion = 0
    private var borrador: Borrador? = null

    private var filaFiltros: LinearLayout? = null
    private var vistaConteo: TextView? = null
    private var contenedorLista: LinearLayout? = null

    private var formulario: FormAdminPreguntaBinding? = null
    private var preguntaEditada: PreguntaAdmin? = null
    private var opciones = listOf<TextInputEditText>()
    private var leccionSeleccionadaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.preguntas_corto))
        consulta = savedInstanceState?.getString("consulta").orEmpty()
        filtro = savedInstanceState?.getInt("filtro") ?: 0
        if (savedInstanceState?.containsKey("borrador_enunciado") == true) {
            borrador = Borrador(
                preguntaId = savedInstanceState.getInt("editando_id", 0).takeIf { it != 0 },
                leccionId = savedInstanceState.getInt("borrador_leccion", 0).takeIf { it != 0 },
                enunciado = savedInstanceState.getString("borrador_enunciado").orEmpty(),
                orden = savedInstanceState.getString("borrador_orden").orEmpty(),
                activo = savedInstanceState.getBoolean("borrador_activo"),
                alternativas = savedInstanceState.getStringArrayList("borrador_alternativas").orEmpty(),
                correcta = savedInstanceState.getInt("borrador_correcta")
            )
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (formulario != null) cargar() else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (formulario == null) cargar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("consulta", consulta)
        outState.putInt("filtro", filtro)
        formulario?.let { vista ->
            outState.putInt("editando_id", preguntaEditada?.id ?: 0)
            outState.putInt("borrador_leccion", leccionSeleccionadaId ?: 0)
            outState.putString("borrador_enunciado", vista.entradaEnunciado.text?.toString().orEmpty())
            outState.putString("borrador_orden", vista.entradaOrden.text?.toString().orEmpty())
            outState.putBoolean("borrador_activo", vista.interruptorActivo.isChecked)
            outState.putStringArrayList(
                "borrador_alternativas",
                ArrayList(opciones.map { it.text?.toString().orEmpty() })
            )
            outState.putInt("borrador_correcta", indiceCorrecta())
        }
        super.onSaveInstanceState(outState)
    }

    private fun cargar() {
        formulario = null
        preguntaEditada = null
        opciones = emptyList()
        val actual = ++generacion
        panel.removeAllViews()
        marco.tituloEncabezado.setText(R.string.preguntas_corto)
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE

        val busqueda = campoTexto(getString(R.string.buscar_registros), consulta)
        filaFiltros = construirFiltros()
        vistaConteo = atenuado("", 12).also { panel.addView(it) }
        contenedorLista = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            .also { panel.addView(it) }
        busqueda.doAfterTextChanged { consulta = it.toString(); dibujar() }

        bottomActions(
            boton(getString(R.string.accion_crear_registro)) { edit(null) }
                .apply { setIconResource(R.drawable.icono_mas) }
        )

        peticion(RetrofitClient.api.leccionesAdmin()) leccionesRequest@{ lista ->
            if (actual != generacion) return@leccionesRequest
            lecciones = lista.orEmpty()
            peticion(RetrofitClient.api.preguntasAdmin()) preguntasRequest@{ listaPreguntas ->
                if (actual != generacion) return@preguntasRequest
                preguntas = listaPreguntas.orEmpty()
                val pendiente = borrador
                if (pendiente != null) {
                    borrador = null
                    edit(
                        pregunta = pendiente.preguntaId?.let {
                            PreguntaAdmin(id = it, idLeccion = pendiente.leccionId ?: 0)
                        },
                        leccionId = pendiente.leccionId,
                        enunciadoInicial = pendiente.enunciado,
                        ordenInicial = pendiente.orden,
                        activoInicial = pendiente.activo,
                        textosIniciales = pendiente.alternativas,
                        correctaInicial = pendiente.correcta
                    )
                } else {
                    dibujar()
                }
            }
        }
    }

    private fun construirFiltros(): LinearLayout {
        val fila = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val etiquetas = listOf(R.string.filtro_todos, R.string.filtro_activos, R.string.filtro_inactivos)
        etiquetas.forEachIndexed { indice, etiqueta ->
            fila.addView(
                secundario(getString(etiqueta)) { filtro = indice; pintarFiltros(); dibujar() }
                    .apply {
                        textSize = 12f
                        minWidth = 0
                        setPadding(dp(4), 0, dp(4), 0)
                    },
                LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(6) }
            )
        }
        panel.addView(fila)
        pintarFiltros()
        return fila
    }

    private fun pintarFiltros() {
        val fila = filaFiltros ?: return
        for (indice in 0 until fila.childCount) {
            val opcion = fila.getChildAt(indice) as? MaterialButton ?: continue
            opcion.backgroundTintList = ColorStateList.valueOf(
                color(if (indice == filtro) R.color.fw_azul else R.color.fw_azul_suave)
            )
            opcion.setTextColor(
                color(if (indice == filtro) R.color.fw_sobre_primario else R.color.fw_azul)
            )
        }
    }

    private fun dibujar() {
        val lista = contenedorLista ?: return
        val conteo = vistaConteo ?: return
        lista.removeAllViews()
        val filtradas = preguntas.filter(::coincide)
        conteo.text = getString(R.string.conteo_resultados_filtro, filtradas.size)
        if (filtradas.isEmpty()) {
            lista.addView(
                atenuado(
                    getString(
                        if (preguntas.isEmpty()) R.string.registros_vacios
                        else R.string.sin_resultados_busqueda
                    )
                )
            )
        }
        filtradas.forEach { pintarPregunta(it, lista) }
    }

    private fun coincide(pregunta: PreguntaAdmin): Boolean {
        val coincideTexto = consulta.isBlank() ||
            pregunta.enunciado.contains(consulta, true) ||
            nombreLeccion(pregunta.idLeccion).contains(consulta, true)
        val activa = pregunta.estado == 1
        return coincideTexto && (filtro == 0 || (filtro == 1 && activa) || (filtro == 2 && !activa))
    }

    private fun nombreLeccion(id: Int): String =
        lecciones.firstOrNull { it.id == id }?.titulo.orEmpty()

    private fun pintarPregunta(pregunta: PreguntaAdmin, lista: LinearLayout) {
        val tarjeta = ItemAdminRecordBinding.inflate(layoutInflater, lista, true)
        tarjeta.avatar.text = pregunta.enunciado.take(1).uppercase()
        tarjeta.tituloRegistro.text = pregunta.enunciado
        tarjeta.detalleRegistro.text = nombreLeccion(pregunta.idLeccion).ifBlank {
            getString(R.string.accion_editar)
        }
        tarjeta.detalleRegistro.maxLines = 2
        tarjeta.estadoRegistro.setText(
            if (pregunta.estado == 1) R.string.estado_activo else R.string.estado_inactivo
        )
        tarjeta.estadoRegistro.setTextColor(
            color(if (pregunta.estado == 1) R.color.fw_verde else R.color.fw_apagado)
        )
        tarjeta.root.setOnClickListener { edit(pregunta) }
    }


    private fun edit(
        pregunta: PreguntaAdmin?,
        leccionId: Int? = pregunta?.idLeccion,
        enunciadoInicial: String = pregunta?.enunciado.orEmpty(),
        ordenInicial: String = (pregunta?.orden ?: 1).toString(),
        activoInicial: Boolean = pregunta?.estado != 0,
        textosIniciales: List<String>? = null,
        correctaInicial: Int? = null
    ) {
        preguntaEditada = pregunta
        ++generacion
        panel.removeAllViews()
        bottomActions()
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE
        marco.tituloEncabezado.setText(
            if (pregunta == null) R.string.titulo_crear_registro else R.string.titulo_editar_registro
        )

        val vista = FormAdminPreguntaBinding.inflate(layoutInflater, panel, true)
        formulario = vista
        vista.entradaEnunciado.setText(enunciadoInicial)
        vista.entradaOrden.setText(ordenInicial)
        vista.interruptorActivo.isChecked = activoInicial
        vista.entradaEnunciado.doAfterTextChanged { vista.campoEnunciado.error = null }

        val antiguas = pregunta?.alternativas.orEmpty()
        val textos = textosIniciales ?: antiguas.map { it.texto }
        val count = maxOf(4, textos.size).coerceAtMost(6)
        vista.contenedorAlternativas.removeAllViews()
        opciones = (0 until count).map { indice ->
            campoTexto(
                getString(R.string.numero_alternativa, indice + 1),
                textos.getOrNull(indice).orEmpty(),
                InputType.TYPE_CLASS_TEXT,
                vista.contenedorAlternativas
            ).apply {
                doAfterTextChanged { (parent as? TextInputLayout)?.error = null }
            }
        }
        val correcta = correctaInicial
            ?: antiguas.indexOfFirst { it.esCorrecta == 1 }.coerceAtLeast(0)
        vista.selectorCorrecta.setSimpleItems(
            (1..count).map { getString(R.string.numero_alternativa, it) }.toTypedArray()
        )
        vista.selectorCorrecta.setText(
            getString(R.string.numero_alternativa, correcta.coerceIn(0, count - 1) + 1),
            false
        )

        vista.selectorLeccion.setSimpleItems(lecciones.map { it.titulo }.toTypedArray())
        val elegida = lecciones.firstOrNull { it.id == leccionId } ?: lecciones.firstOrNull()
        leccionSeleccionadaId = elegida?.id
        if (elegida != null) vista.selectorLeccion.setText(elegida.titulo, false)
        vista.selectorLeccion.setOnItemClickListener { _, _, posicion, _ ->
            leccionSeleccionadaId = lecciones.getOrNull(posicion)?.id
            vista.campoLeccion.error = null
        }

        if (pregunta != null) {
            panel.addView(
                contorno(getString(R.string.accion_desactivar_registro)) { confirmarDesactivar() }
                    .apply {
                        setTextColor(color(R.color.fw_peligro))
                        strokeColor = ColorStateList.valueOf(color(R.color.fw_borde))
                    }
            )
        }

        bottomActions(
            contorno(getString(R.string.accion_cancelar)) { cargar() },
            boton(getString(R.string.accion_guardar_cambios)) { guardar() }
        )
        marco.scrollContenido.post { marco.scrollContenido.scrollTo(0, 0) }
    }

    private fun indiceCorrecta(): Int {
        val vista = formulario ?: return 0
        val texto = vista.selectorCorrecta.text?.toString()
        return opciones.indices.firstOrNull {
            texto == getString(R.string.numero_alternativa, it + 1)
        } ?: 0
    }

    private fun guardar() {
        if (progreso.visibility == View.VISIBLE) return
        val vista = formulario ?: return

        val idLeccion = leccionSeleccionadaId
        if (idLeccion == null) {
            vista.campoLeccion.error = getString(R.string.error_selecciona_leccion)
            return
        }
        vista.campoLeccion.error = null

        val enunciado = vista.entradaEnunciado.text?.toString()?.trim().orEmpty()
        if (enunciado.isBlank()) {
            vista.campoEnunciado.error = getString(R.string.error_enunciado_requerido)
            return
        }
        vista.campoEnunciado.error = null

        val orden = vista.entradaOrden.text?.toString()?.trim()?.toIntOrNull()
        if (orden == null || orden < 1) {
            vista.campoOrden.error = getString(R.string.error_orden_positivo)
            return
        }
        vista.campoOrden.error = null

        val textos = opciones.map { it.text?.toString()?.trim().orEmpty() }
        val vacia = textos.indexOfFirst { it.isBlank() }
        if (vacia >= 0) {
            (opciones[vacia].parent as? TextInputLayout)?.error =
                getString(R.string.error_alternativas_requeridas)
            return
        }

        val correcta = indiceCorrecta()
        val pregunta = PreguntaAdmin(
            id = preguntaEditada?.id,
            idLeccion = idLeccion,
            enunciado = enunciado,
            orden = orden,
            estado = if (vista.interruptorActivo.isChecked) 1 else 0,
            alternativas = textos.mapIndexed { indice, texto ->
                AlternativaAdmin(texto = texto, esCorrecta = if (indice == correcta) 1 else 0)
            }
        )
        val llamada =
            if (preguntaEditada == null) RetrofitClient.api.preguntaCrear(pregunta)
            else RetrofitClient.api.preguntaActualizar(pregunta)
        peticion(llamada) { cargar() }
    }

    private fun confirmarDesactivar() {
        val id = preguntaEditada?.id ?: return
        AlertDialog.Builder(this)
            .setTitle(R.string.accion_desactivar_registro)
            .setMessage(R.string.confirmacion_desactivar_registro)
            .setNegativeButton(R.string.accion_cancelar, null)
            .setPositiveButton(R.string.accion_desactivar) { _, _ ->
                peticion(RetrofitClient.api.preguntaDesactivar(IdPregunta(id))) { cargar() }
            }
            .show()
    }
}

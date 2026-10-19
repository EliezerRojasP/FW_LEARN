package com.example.four_ways_learn.admin

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.FormAdminTemaBinding
import com.example.four_ways_learn.databinding.ItemAdminRecordBinding
import com.example.four_ways_learn.model.CursoAdmin
import com.example.four_ways_learn.model.IdTopico
import com.example.four_ways_learn.model.TopicoAdmin
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import com.google.android.material.button.MaterialButton

class AdminTemasActivity : BaseActivity() {
    override val requiereAdmin = true

    private class Borrador(
        val temaId: Int?,
        val cursoId: Int?,
        val nombre: String,
        val descripcion: String,
        val orden: String,
        val activo: Boolean
    )

    private var cursos = listOf<CursoAdmin>()
    private var temas = listOf<TopicoAdmin>()
    private var consulta = ""
    private var filtro = 0
    private var generacion = 0
    private var borrador: Borrador? = null

    private var filaFiltros: LinearLayout? = null
    private var vistaConteo: TextView? = null
    private var contenedorLista: LinearLayout? = null

    private var formulario: FormAdminTemaBinding? = null
    private var temaEditado: TopicoAdmin? = null
    private var cursoSeleccionadoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.modulo_temas))
        consulta = savedInstanceState?.getString("consulta").orEmpty()
        filtro = savedInstanceState?.getInt("filtro") ?: 0
        if (savedInstanceState?.containsKey("borrador_nombre") == true) {
            val temaId = savedInstanceState.getInt("editando_id", 0).takeIf { it != 0 }
            val cursoId = savedInstanceState.getInt("borrador_curso", 0).takeIf { it != 0 }
            borrador = Borrador(
                temaId = temaId,
                cursoId = cursoId,
                nombre = savedInstanceState.getString("borrador_nombre").orEmpty(),
                descripcion = savedInstanceState.getString("borrador_descripcion").orEmpty(),
                orden = savedInstanceState.getString("borrador_orden").orEmpty(),
                activo = savedInstanceState.getBoolean("borrador_activo")
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
            outState.putInt("editando_id", temaEditado?.id ?: 0)
            outState.putInt("borrador_curso", cursoSeleccionadoId ?: 0)
            outState.putString("borrador_nombre", vista.entradaNombre.text?.toString().orEmpty())
            outState.putString(
                "borrador_descripcion",
                vista.entradaDescripcion.text?.toString().orEmpty()
            )
            outState.putString("borrador_orden", vista.entradaOrden.text?.toString().orEmpty())
            outState.putBoolean("borrador_activo", vista.interruptorActivo.isChecked)
        }
        super.onSaveInstanceState(outState)
    }

    private fun cargar() {
        formulario = null
        temaEditado = null
        val actual = ++generacion
        panel.removeAllViews()
        marco.tituloEncabezado.setText(R.string.modulo_temas)
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

        peticion(RetrofitClient.api.cursosAdmin()) { lista ->
            if (actual != generacion) return@peticion
            cursos = lista.orEmpty()
            val pendiente = borrador
            if (pendiente != null) {
                borrador = null
                edit(
                    tema = pendiente.temaId?.let { TopicoAdmin(id = it, idCurso = pendiente.cursoId ?: 0) },
                    cursoId = pendiente.cursoId,
                    nombreInicial = pendiente.nombre,
                    descripcionInicial = pendiente.descripcion,
                    ordenInicial = pendiente.orden,
                    activoInicial = pendiente.activo
                )
            } else {
                cargarTemas(actual)
            }
        }
    }

    private fun cargarTemas(actual: Int) {
        peticion(RetrofitClient.api.topicosAdmin()) { lista ->
            if (actual != generacion) return@peticion
            temas = lista.orEmpty()
            dibujar()
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
        val filtrados = temas.filter(::coincide)
        conteo.text = getString(R.string.conteo_resultados_filtro, filtrados.size)
        if (filtrados.isEmpty()) {
            lista.addView(
                atenuado(
                    getString(
                        if (temas.isEmpty()) R.string.registros_vacios
                        else R.string.sin_resultados_busqueda
                    )
                )
            )
        }
        filtrados.forEach { pintarTema(it, lista) }
    }

    private fun coincide(tema: TopicoAdmin): Boolean {
        val coincideTexto = consulta.isBlank() ||
            tema.nombre.contains(consulta, true) ||
            tema.descripcion?.contains(consulta, true) == true ||
            nombreCurso(tema.idCurso).contains(consulta, true)
        val activo = tema.estado == 1
        return coincideTexto && (filtro == 0 || (filtro == 1 && activo) || (filtro == 2 && !activo))
    }

    private fun nombreCurso(id: Int): String =
        cursos.firstOrNull { it.id == id }?.nombre.orEmpty()

    private fun pintarTema(tema: TopicoAdmin, lista: LinearLayout) {
        val tarjeta = ItemAdminRecordBinding.inflate(layoutInflater, lista, true)
        tarjeta.avatar.text = tema.nombre.take(1).uppercase()
        tarjeta.tituloRegistro.text = tema.nombre
        tarjeta.detalleRegistro.text =
            tema.descripcion?.ifBlank { nombreCurso(tema.idCurso) } ?: nombreCurso(tema.idCurso)
        tarjeta.detalleRegistro.maxLines = 2
        tarjeta.estadoRegistro.setText(
            if (tema.estado == 1) R.string.estado_activo else R.string.estado_inactivo
        )
        tarjeta.estadoRegistro.setTextColor(
            color(if (tema.estado == 1) R.color.fw_verde else R.color.fw_apagado)
        )
        tarjeta.root.setOnClickListener { edit(tema) }
    }

    private fun edit(
        tema: TopicoAdmin?,
        cursoId: Int? = tema?.idCurso,
        nombreInicial: String = tema?.nombre.orEmpty(),
        descripcionInicial: String = tema?.descripcion.orEmpty(),
        ordenInicial: String = (tema?.orden ?: 1).toString(),
        activoInicial: Boolean = tema?.estado != 0
    ) {
        temaEditado = tema
        ++generacion
        panel.removeAllViews()
        bottomActions()
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE
        marco.tituloEncabezado.setText(
            if (tema == null) R.string.titulo_crear_registro else R.string.titulo_editar_registro
        )

        val vista = FormAdminTemaBinding.inflate(layoutInflater, panel, true)
        vista.selectorCurso.setSimpleItems(cursos.map { it.nombre }.toTypedArray())
        val cursoElegido = cursos.firstOrNull { it.id == cursoId } ?: cursos.firstOrNull()
        cursoSeleccionadoId = cursoElegido?.id
        if (cursoElegido != null) vista.selectorCurso.setText(cursoElegido.nombre, false)
        vista.selectorCurso.setOnItemClickListener { _, _, posicion, _ ->
            cursoSeleccionadoId = cursos.getOrNull(posicion)?.id
            vista.campoCurso.error = null
        }
        vista.entradaNombre.setText(nombreInicial)
        vista.entradaDescripcion.setText(descripcionInicial)
        vista.entradaOrden.setText(ordenInicial)
        vista.interruptorActivo.isChecked = activoInicial
        vista.entradaNombre.doAfterTextChanged { vista.campoNombre.error = null }
        formulario = vista

        if (tema != null) {
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

    private fun guardar() {
        if (progreso.visibility == View.VISIBLE) return
        val vista = formulario ?: return

        val idCurso = cursoSeleccionadoId
        if (idCurso == null) {
            vista.campoCurso.error = getString(R.string.error_selecciona_curso)
            return
        }
        vista.campoCurso.error = null

        val nombre = vista.entradaNombre.text?.toString()?.trim().orEmpty()
        if (nombre.isBlank()) {
            vista.campoNombre.error = getString(R.string.error_nombre_requerido)
            return
        }
        vista.campoNombre.error = null

        val orden = vista.entradaOrden.text?.toString()?.trim()?.toIntOrNull()
        if (orden == null || orden < 1) {
            vista.campoOrden.error = getString(R.string.error_orden_positivo)
            return
        }
        vista.campoOrden.error = null

        val tema = TopicoAdmin(
            id = temaEditado?.id,
            idCurso = idCurso,
            nombre = nombre,
            descripcion = vista.entradaDescripcion.text?.toString()?.trim().orEmpty(),
            orden = orden,
            estado = if (vista.interruptorActivo.isChecked) 1 else 0
        )
        val llamada =
            if (temaEditado == null) RetrofitClient.api.topicoCrear(tema)
            else RetrofitClient.api.topicoActualizar(tema)
        peticion(llamada) { cargar() }
    }

    private fun confirmarDesactivar() {
        val id = temaEditado?.id ?: return
        AlertDialog.Builder(this)
            .setTitle(R.string.accion_desactivar_registro)
            .setMessage(R.string.confirmacion_desactivar_registro)
            .setNegativeButton(R.string.accion_cancelar, null)
            .setPositiveButton(R.string.accion_desactivar) { _, _ ->
                peticion(RetrofitClient.api.topicoDesactivar(IdTopico(id))) { cargar() }
            }
            .show()
    }
}

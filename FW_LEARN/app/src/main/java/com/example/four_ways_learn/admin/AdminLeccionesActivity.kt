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
import com.example.four_ways_learn.databinding.FormAdminLeccionBinding
import com.example.four_ways_learn.databinding.ItemAdminRecordBinding
import com.example.four_ways_learn.model.CursoAdmin
import com.example.four_ways_learn.model.IdLeccion
import com.example.four_ways_learn.model.LeccionAdmin
import com.example.four_ways_learn.model.TopicoAdmin
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import com.example.four_ways_learn.ui.LessonSections
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout

class AdminLeccionesActivity : BaseActivity() {
    override val requiereAdmin = true

    private class Borrador(
        val leccionId: Int?,
        val cursoId: Int?,
        val topicoId: Int?,
        val titulo: String,
        val orden: String,
        val activo: Boolean,
        val contenidoOriginal: String,
        val contenido: String,
        val ejemplo: String,
        val practica: String,
        val explicacion: String
    )

    private var cursos = listOf<CursoAdmin>()
    private var temas = listOf<TopicoAdmin>()
    private var lecciones = listOf<LeccionAdmin>()
    private var temasVisibles = listOf<TopicoAdmin>()
    private var consulta = ""
    private var filtro = 0
    private var generacion = 0
    private var borrador: Borrador? = null

    private var filaFiltros: LinearLayout? = null
    private var vistaConteo: TextView? = null
    private var contenedorLista: LinearLayout? = null

    private var formulario: FormAdminLeccionBinding? = null
    private var leccionEditada: LeccionAdmin? = null
    private var secciones: LessonSections? = null
    private var contenidoBase = ""
    private var cursoSeleccionadoId: Int? = null
    private var temaSeleccionadoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.modulo_lecciones))
        consulta = savedInstanceState?.getString("consulta").orEmpty()
        filtro = savedInstanceState?.getInt("filtro") ?: 0
        if (savedInstanceState?.containsKey("borrador_titulo") == true) {
            borrador = Borrador(
                leccionId = savedInstanceState.getInt("editando_id", 0).takeIf { it != 0 },
                cursoId = savedInstanceState.getInt("borrador_curso", 0).takeIf { it != 0 },
                topicoId = savedInstanceState.getInt("borrador_topico", 0).takeIf { it != 0 },
                titulo = savedInstanceState.getString("borrador_titulo").orEmpty(),
                orden = savedInstanceState.getString("borrador_orden").orEmpty(),
                activo = savedInstanceState.getBoolean("borrador_activo"),
                contenidoOriginal = savedInstanceState.getString("borrador_original").orEmpty(),
                contenido = savedInstanceState.getString("borrador_contenido").orEmpty(),
                ejemplo = savedInstanceState.getString("borrador_ejemplo").orEmpty(),
                practica = savedInstanceState.getString("borrador_practica").orEmpty(),
                explicacion = savedInstanceState.getString("borrador_explicacion").orEmpty()
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
            outState.putInt("editando_id", leccionEditada?.id ?: 0)
            outState.putInt("borrador_curso", cursoSeleccionadoId ?: 0)
            outState.putInt("borrador_topico", temaSeleccionadoId ?: 0)
            outState.putString("borrador_titulo", vista.entradaTitulo.text?.toString().orEmpty())
            outState.putString("borrador_orden", vista.entradaOrden.text?.toString().orEmpty())
            outState.putBoolean("borrador_activo", vista.interruptorActivo.isChecked)
            outState.putString("borrador_original", contenidoBase)
            outState.putString(
                "borrador_contenido",
                vista.entradaSeccionContenido.text?.toString().orEmpty()
            )
            outState.putString(
                "borrador_ejemplo",
                vista.entradaSeccionEjemplo.text?.toString().orEmpty()
            )
            outState.putString(
                "borrador_practica",
                vista.entradaSeccionPractica.text?.toString().orEmpty()
            )
            outState.putString(
                "borrador_explicacion",
                vista.entradaSeccionExplicacion.text?.toString().orEmpty()
            )
        }
        super.onSaveInstanceState(outState)
    }

    private fun cargar() {
        formulario = null
        leccionEditada = null
        secciones = null
        contenidoBase = ""
        val actual = ++generacion
        panel.removeAllViews()
        marco.tituloEncabezado.setText(R.string.modulo_lecciones)
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

        peticion(RetrofitClient.api.cursosAdmin()) cursosRequest@{ lista ->
            if (actual != generacion) return@cursosRequest
            cursos = lista.orEmpty()
            peticion(RetrofitClient.api.topicosAdmin()) temasRequest@{ listaTemas ->
                if (actual != generacion) return@temasRequest
                temas = listaTemas.orEmpty()
                peticion(RetrofitClient.api.leccionesAdmin()) leccionesRequest@{ listaLecciones ->
                    if (actual != generacion) return@leccionesRequest
                    lecciones = listaLecciones.orEmpty()
                    val pendiente = borrador
                    if (pendiente != null) {
                        borrador = null
                        edit(
                            leccion = pendiente.leccionId?.let {
                                LeccionAdmin(id = it, idTopico = pendiente.topicoId ?: 0)
                            },
                            topicoId = pendiente.topicoId,
                            cursoId = pendiente.cursoId,
                            tituloInicial = pendiente.titulo,
                            ordenInicial = pendiente.orden,
                            activoInicial = pendiente.activo,
                            contenidoOriginal = pendiente.contenidoOriginal,
                            contenidoInicial = pendiente.contenido,
                            ejemploInicial = pendiente.ejemplo,
                            practicaInicial = pendiente.practica,
                            explicacionInicial = pendiente.explicacion
                        )
                    } else {
                        dibujar()
                    }
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
        val filtradas = lecciones.filter(::coincide)
        conteo.text = getString(R.string.conteo_resultados_filtro, filtradas.size)
        if (filtradas.isEmpty()) {
            lista.addView(
                atenuado(
                    getString(
                        if (lecciones.isEmpty()) R.string.registros_vacios
                        else R.string.sin_resultados_busqueda
                    )
                )
            )
        }
        filtradas.forEach { pintarLeccion(it, lista) }
    }

    private fun coincide(leccion: LeccionAdmin): Boolean {
        val coincideTexto = consulta.isBlank() ||
            leccion.titulo.contains(consulta, true) ||
            nombreTema(leccion.idTopico).contains(consulta, true)
        val activa = leccion.estado == 1
        return coincideTexto && (filtro == 0 || (filtro == 1 && activa) || (filtro == 2 && !activa))
    }

    private fun nombreTema(id: Int): String =
        temas.firstOrNull { it.id == id }?.nombre.orEmpty()

    private fun pintarLeccion(leccion: LeccionAdmin, lista: LinearLayout) {
        val tarjeta = ItemAdminRecordBinding.inflate(layoutInflater, lista, true)
        tarjeta.avatar.text = leccion.titulo.take(1).uppercase()
        tarjeta.tituloRegistro.text = leccion.titulo
        tarjeta.detalleRegistro.text = nombreTema(leccion.idTopico).ifBlank {
            getString(R.string.accion_editar)
        }
        tarjeta.detalleRegistro.maxLines = 2
        tarjeta.estadoRegistro.setText(
            if (leccion.estado == 1) R.string.estado_activo else R.string.estado_inactivo
        )
        tarjeta.estadoRegistro.setTextColor(
            color(if (leccion.estado == 1) R.color.fw_verde else R.color.fw_apagado)
        )
        tarjeta.root.setOnClickListener { edit(leccion) }
    }

    private fun edit(
        leccion: LeccionAdmin?,
        topicoId: Int? = leccion?.idTopico,
        cursoId: Int? = null,
        tituloInicial: String = leccion?.titulo.orEmpty(),
        ordenInicial: String = (leccion?.orden ?: 1).toString(),
        activoInicial: Boolean = leccion?.estado != 0,
        contenidoOriginal: String = leccion?.contenido.orEmpty(),
        contenidoInicial: String? = null,
        ejemploInicial: String? = null,
        practicaInicial: String? = null,
        explicacionInicial: String? = null
    ) {
        leccionEditada = leccion
        ++generacion
        panel.removeAllViews()
        bottomActions()
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE
        marco.tituloEncabezado.setText(
            if (leccion == null) R.string.editor_nueva_leccion else R.string.editor_leccion
        )

        val analizado = LessonSections.parse(contenidoOriginal)
        secciones = analizado
        contenidoBase = contenidoOriginal

        val vista = FormAdminLeccionBinding.inflate(layoutInflater, panel, true)
        vista.entradaTitulo.setText(tituloInicial)
        vista.entradaOrden.setText(ordenInicial)
        vista.entradaSeccionContenido.setText(contenidoInicial ?: analizado.content)
        vista.entradaSeccionEjemplo.setText(ejemploInicial ?: analizado.example)
        vista.entradaSeccionPractica.setText(practicaInicial ?: analizado.practice)
        vista.entradaSeccionExplicacion.setText(explicacionInicial ?: analizado.explanation)
        vista.interruptorActivo.isChecked = activoInicial
        vista.entradaTitulo.doAfterTextChanged { vista.campoTitulo.error = null }
        formulario = vista

        vista.selectorCurso.setSimpleItems(cursos.map { it.nombre }.toTypedArray())
        val temaInicial = temas.firstOrNull { it.id == topicoId }
        val cursoInicial = cursos.firstOrNull { it.id == cursoId }
            ?: cursos.firstOrNull { it.id == temaInicial?.idCurso }
            ?: cursos.firstOrNull()
        cursoSeleccionadoId = cursoInicial?.id
        if (cursoInicial != null) vista.selectorCurso.setText(cursoInicial.nombre, false)
        actualizarTemas(temaInicial?.id)
        vista.selectorCurso.setOnItemClickListener { _, _, posicion, _ ->
            cursoSeleccionadoId = cursos.getOrNull(posicion)?.id
            vista.campoCurso.error = null
            actualizarTemas()
        }

        vista.tabSecciones.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mostrarSeccion(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        mostrarSeccion(0)

        if (leccion != null) {
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

    private fun actualizarTemas(idTopicoBuscado: Int? = null) {
        val vista = formulario ?: return
        temasVisibles = temas.filter { it.idCurso == cursoSeleccionadoId }
        vista.selectorTema.setSimpleItems(temasVisibles.map { it.nombre }.toTypedArray())
        val elegido = temasVisibles.firstOrNull { it.id == idTopicoBuscado } ?: temasVisibles.firstOrNull()
        temaSeleccionadoId = elegido?.id
        vista.selectorTema.setText(elegido?.nombre, false)
        vista.selectorTema.setOnItemClickListener { _, _, posicion, _ ->
            temaSeleccionadoId = temasVisibles.getOrNull(posicion)?.id
            vista.campoTema.error = null
        }
    }

    private fun mostrarSeccion(indice: Int) {
        val vista = formulario ?: return
        vista.grupoContenido.visibility = if (indice == 0) View.VISIBLE else View.GONE
        vista.grupoEjemplo.visibility = if (indice == 1) View.VISIBLE else View.GONE
        vista.grupoPractica.visibility = if (indice == 2) View.VISIBLE else View.GONE
    }

    private fun guardar() {
        if (progreso.visibility == View.VISIBLE) return
        val vista = formulario ?: return

        val idTopico = temaSeleccionadoId
        if (idTopico == null) {
            vista.campoTema.error = getString(R.string.error_selecciona_tema)
            return
        }
        vista.campoTema.error = null

        val titulo = vista.entradaTitulo.text?.toString()?.trim().orEmpty()
        if (titulo.isBlank()) {
            vista.campoTitulo.error = getString(R.string.error_titulo_requerido)
            return
        }
        vista.campoTitulo.error = null

        val orden = vista.entradaOrden.text?.toString()?.trim()?.toIntOrNull()
        if (orden == null || orden < 1) {
            vista.campoOrden.error = getString(R.string.error_orden_positivo)
            return
        }
        vista.campoOrden.error = null

        val contenido = (secciones ?: LessonSections.parse("")).compose(
            vista.entradaSeccionContenido.text?.toString().orEmpty(),
            vista.entradaSeccionEjemplo.text?.toString().orEmpty(),
            vista.entradaSeccionPractica.text?.toString().orEmpty(),
            vista.entradaSeccionExplicacion.text?.toString().orEmpty()
        )

        val leccion = LeccionAdmin(
            id = leccionEditada?.id,
            idTopico = idTopico,
            titulo = titulo,
            contenido = contenido,
            orden = orden,
            estado = if (vista.interruptorActivo.isChecked) 1 else 0
        )
        val llamada =
            if (leccionEditada == null) RetrofitClient.api.leccionCrear(leccion)
            else RetrofitClient.api.leccionActualizar(leccion)
        peticion(llamada) { cargar() }
    }

    private fun confirmarDesactivar() {
        val id = leccionEditada?.id ?: return
        AlertDialog.Builder(this)
            .setTitle(R.string.accion_desactivar_registro)
            .setMessage(R.string.confirmacion_desactivar_registro)
            .setNegativeButton(R.string.accion_cancelar, null)
            .setPositiveButton(R.string.accion_desactivar) { _, _ ->
                peticion(RetrofitClient.api.leccionDesactivar(IdLeccion(id))) { cargar() }
            }
            .show()
    }
}

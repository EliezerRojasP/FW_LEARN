package com.example.four_ways_learn.admin

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.FormAdminCursoBinding
import com.example.four_ways_learn.databinding.ListaAdminBinding
import com.example.four_ways_learn.model.CursoAdmin
import com.example.four_ways_learn.model.IdCurso
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity


class AdminCursosActivity : BaseActivity() {
    override val requiereAdmin = true

    private var cursos = listOf<CursoAdmin>()
    private var consulta = ""
    private var filtro = 0
    private var generacion = 0

    private var vistaLista: ListaAdminBinding? = null
    private val adaptador = CursoAdapter { curso -> edit(curso) }

    private var formulario: FormAdminCursoBinding? = null
    private var cursoEditado: CursoAdmin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.modulo_cursos))
        consulta = savedInstanceState?.getString("consulta").orEmpty()
        filtro = savedInstanceState?.getInt("filtro") ?: 0
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (formulario != null) cargar() else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        if (savedInstanceState?.containsKey("borrador_nombre") == true) {
            val id = savedInstanceState.getInt("editando_id", 0)
            edit(
                curso = if (id == 0) null else CursoAdmin(id = id),
                nombreInicial = savedInstanceState.getString("borrador_nombre").orEmpty(),
                descripcionInicial = savedInstanceState.getString("borrador_descripcion").orEmpty(),
                activoInicial = savedInstanceState.getBoolean("borrador_activo")
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (formulario == null) cargar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("consulta", consulta)
        outState.putInt("filtro", filtro)
        formulario?.let { vista ->
            outState.putInt("editando_id", cursoEditado?.id ?: 0)
            outState.putString("borrador_nombre", vista.entradaNombre.text?.toString().orEmpty())
            outState.putString(
                "borrador_descripcion",
                vista.entradaDescripcion.text?.toString().orEmpty()
            )
            outState.putBoolean("borrador_activo", vista.interruptorActivo.isChecked)
        }
        super.onSaveInstanceState(outState)
    }


    private fun cargar() {
        formulario = null
        cursoEditado = null
        val actual = ++generacion
        mostrarLista()
        panel.removeAllViews()
        vistaLista?.listaRegistros?.adapter = null
        contenidoLista.removeAllViews()
        marco.tituloEncabezado.setText(R.string.modulo_cursos)
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE

        val vista = ListaAdminBinding.inflate(layoutInflater, contenidoLista, true)
        vistaLista = vista
        vista.entradaBusqueda.setText(consulta)
        vista.entradaBusqueda.doAfterTextChanged { consulta = it.toString(); dibujar() }
        vista.listaRegistros.layoutManager = LinearLayoutManager(this)
        vista.listaRegistros.adapter = adaptador
        vista.filtroTodos.setOnClickListener { filtro = 0; pintarFiltros(); dibujar() }
        vista.filtroActivos.setOnClickListener { filtro = 1; pintarFiltros(); dibujar() }
        vista.filtroInactivos.setOnClickListener { filtro = 2; pintarFiltros(); dibujar() }
        pintarFiltros()

        bottomActions(
            boton(getString(R.string.accion_crear_registro)) { edit(null) }
                .apply { setIconResource(R.drawable.icono_mas) }
        )

        peticion(RetrofitClient.api.cursosAdmin()) { data ->
            if (actual != generacion) return@peticion
            cursos = data.orEmpty()
            dibujar()
        }
    }

    private fun pintarFiltros() {
        val vista = vistaLista ?: return
        val opciones = listOf(
            vista.filtroTodos to 0,
            vista.filtroActivos to 1,
            vista.filtroInactivos to 2
        )
        opciones.forEach { (boton, indice) ->
            boton.backgroundTintList = ColorStateList.valueOf(
                color(if (indice == filtro) R.color.fw_azul else R.color.fw_azul_suave)
            )
            boton.setTextColor(
                color(if (indice == filtro) R.color.fw_sobre_primario else R.color.fw_azul)
            )
        }
    }

    private fun dibujar() {
        val vista = vistaLista ?: return
        val filtrados = cursos.filter(::coincide)
        vista.textoConteo.text = getString(R.string.conteo_resultados_filtro, filtrados.size)
        vista.textoVacio.setText(
            if (cursos.isEmpty()) R.string.registros_vacios else R.string.sin_resultados_busqueda
        )
        vista.textoVacio.visibility = if (filtrados.isEmpty()) View.VISIBLE else View.GONE
        adaptador.actualizar(filtrados)
    }

    private fun coincide(curso: CursoAdmin): Boolean {
        val coincideTexto = consulta.isBlank() ||
            curso.nombre.contains(consulta, true) ||
            curso.descripcion?.contains(consulta, true) == true
        val activo = curso.estado == 1
        return coincideTexto && (filtro == 0 || (filtro == 1 && activo) || (filtro == 2 && !activo))
    }


    private fun edit(
        curso: CursoAdmin?,
        nombreInicial: String = curso?.nombre.orEmpty(),
        descripcionInicial: String = curso?.descripcion.orEmpty(),
        activoInicial: Boolean = curso?.estado != 0
    ) {
        cursoEditado = curso
        ++generacion
        vistaLista?.listaRegistros?.adapter = null
        vistaLista = null
        mostrarFormulario()
        panel.removeAllViews()
        bottomActions()
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE
        marco.tituloEncabezado.setText(
            if (curso == null) R.string.titulo_crear_registro else R.string.titulo_editar_registro
        )

        val vista = FormAdminCursoBinding.inflate(layoutInflater, panel, true)
        vista.entradaNombre.setText(nombreInicial)
        vista.entradaDescripcion.setText(descripcionInicial)
        vista.interruptorActivo.isChecked = activoInicial
        vista.entradaNombre.doAfterTextChanged { vista.campoNombre.error = null }
        formulario = vista

        if (curso != null) {
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
        val nombre = vista.entradaNombre.text?.toString()?.trim().orEmpty()
        if (nombre.isBlank()) {
            vista.campoNombre.error = getString(R.string.error_nombre_requerido)
            return
        }
        vista.campoNombre.error = null

        val curso = CursoAdmin(
            id = cursoEditado?.id,
            nombre = nombre,
            descripcion = vista.entradaDescripcion.text?.toString()?.trim().orEmpty(),
            estado = if (vista.interruptorActivo.isChecked) 1 else 0
        )
        val llamada =
            if (cursoEditado == null) RetrofitClient.api.cursoCrear(curso)
            else RetrofitClient.api.cursoActualizar(curso)
        peticion(llamada) { cargar() }
    }

    private fun confirmarDesactivar() {
        val id = cursoEditado?.id ?: return
        AlertDialog.Builder(this)
            .setTitle(R.string.accion_desactivar_registro)
            .setMessage(R.string.confirmacion_desactivar_registro)
            .setNegativeButton(R.string.accion_cancelar, null)
            .setPositiveButton(R.string.accion_desactivar) { _, _ ->
                peticion(RetrofitClient.api.cursoDesactivar(IdCurso(id))) { cargar() }
            }
            .show()
    }
}

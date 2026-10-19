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
import com.example.four_ways_learn.databinding.FormAdminUsuarioBinding
import com.example.four_ways_learn.databinding.ItemAdminRecordBinding
import com.example.four_ways_learn.model.IdUsuario
import com.example.four_ways_learn.model.UsuarioAdmin
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import com.google.android.material.button.MaterialButton

class AdminUsuariosActivity : BaseActivity() {
    override val requiereAdmin = true

    private class Borrador(
        val usuarioId: Int?,
        val nombre: String,
        val apellido: String,
        val usuario: String,
        val password: String,
        val idRol: Int,
        val activo: Boolean
    )

    private var usuarios = listOf<UsuarioAdmin>()
    private var consulta = ""
    private var filtro = 0
    private var generacion = 0
    private var borrador: Borrador? = null

    private var filaFiltros: LinearLayout? = null
    private var vistaConteo: TextView? = null
    private var contenedorLista: LinearLayout? = null

    private var formulario: FormAdminUsuarioBinding? = null
    private var usuarioEditado: UsuarioAdmin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.modulo_usuarios))
        consulta = savedInstanceState?.getString("consulta").orEmpty()
        filtro = savedInstanceState?.getInt("filtro") ?: 0
        if (savedInstanceState?.containsKey("borrador_usuario") == true) {
            borrador = Borrador(
                usuarioId = savedInstanceState.getInt("editando_id", 0).takeIf { it != 0 },
                nombre = savedInstanceState.getString("borrador_nombre").orEmpty(),
                apellido = savedInstanceState.getString("borrador_apellido").orEmpty(),
                usuario = savedInstanceState.getString("borrador_usuario").orEmpty(),
                password = savedInstanceState.getString("borrador_password").orEmpty(),
                idRol = savedInstanceState.getInt("borrador_rol", UsuarioAdmin.ROL_ALUMNO),
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
            outState.putInt("editando_id", usuarioEditado?.id ?: 0)
            outState.putString("borrador_nombre", vista.entradaNombre.text?.toString().orEmpty())
            outState.putString("borrador_apellido", vista.entradaApellido.text?.toString().orEmpty())
            outState.putString("borrador_usuario", vista.entradaUsuario.text?.toString().orEmpty())
            outState.putString("borrador_password", vista.entradaContrasena.text?.toString().orEmpty())
            outState.putInt("borrador_rol", rolSeleccionado(vista.selectorRol.text?.toString()))
            outState.putBoolean("borrador_activo", vista.interruptorActivo.isChecked)
        }
        super.onSaveInstanceState(outState)
    }


    private fun cargar() {
        formulario = null
        usuarioEditado = null
        val actual = ++generacion
        panel.removeAllViews()
        marco.tituloEncabezado.setText(R.string.modulo_usuarios)
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE

        val busqueda = campoTexto(getString(R.string.buscar_usuarios), consulta)
        filaFiltros = construirFiltros()
        vistaConteo = atenuado("", 12).also { panel.addView(it) }
        contenedorLista = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            .also { panel.addView(it) }
        busqueda.doAfterTextChanged { consulta = it.toString(); dibujar() }

        bottomActions(
            boton(getString(R.string.accion_crear_usuario)) { edit(null) }
                .apply { setIconResource(R.drawable.icono_mas) }
        )

        peticion(RetrofitClient.api.usuariosAdmin()) { lista ->
            if (actual != generacion) return@peticion
            usuarios = lista.orEmpty()
            val pendiente = borrador
            if (pendiente != null) {
                borrador = null
                edit(
                    usuario = pendiente.usuarioId?.let { UsuarioAdmin(id = it) },
                    nombreInicial = pendiente.nombre,
                    apellidoInicial = pendiente.apellido,
                    usuarioInicial = pendiente.usuario,
                    passwordInicial = pendiente.password,
                    rolInicial = pendiente.idRol,
                    activoInicial = pendiente.activo
                )
            } else {
                dibujar()
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
        val filtrados = usuarios.filter(::coincide)
        conteo.text = getString(R.string.conteo_resultados_filtro, filtrados.size)
        if (filtrados.isEmpty()) {
            lista.addView(
                atenuado(
                    getString(
                        if (usuarios.isEmpty()) R.string.registros_vacios
                        else R.string.sin_resultados_busqueda
                    )
                )
            )
        }
        filtrados.forEach { pintarUsuario(it, lista) }
    }

    private fun coincide(usuario: UsuarioAdmin): Boolean {
        val coincideTexto = consulta.isBlank() ||
            usuario.nombre.contains(consulta, true) ||
            usuario.apellido.contains(consulta, true) ||
            usuario.usuario.contains(consulta, true)
        val activo = usuario.estado == 1
        return coincideTexto && (filtro == 0 || (filtro == 1 && activo) || (filtro == 2 && !activo))
    }

    private fun pintarUsuario(usuario: UsuarioAdmin, lista: LinearLayout) {
        val tarjeta = ItemAdminRecordBinding.inflate(layoutInflater, lista, true)
        tarjeta.avatar.text = usuario.nombre.take(1).uppercase()
        tarjeta.tituloRegistro.text = usuario.usuario
        tarjeta.detalleRegistro.text = getString(
            R.string.detalle_usuario_admin,
            getString(if (usuario.idRol == UsuarioAdmin.ROL_ADMIN) R.string.rol_administrador else R.string.rol_alumno),
            getString(R.string.identidad_usuario_admin, usuario.nombre, usuario.apellido).trim()
        )
        tarjeta.estadoRegistro.setText(
            if (usuario.estado == 1) R.string.estado_activo else R.string.estado_inactivo
        )
        tarjeta.estadoRegistro.setTextColor(
            color(if (usuario.estado == 1) R.color.fw_verde else R.color.fw_apagado)
        )
        tarjeta.root.setOnClickListener { edit(usuario) }
    }


    private fun edit(
        usuario: UsuarioAdmin?,
        nombreInicial: String = usuario?.nombre.orEmpty(),
        apellidoInicial: String = usuario?.apellido.orEmpty(),
        usuarioInicial: String = usuario?.usuario.orEmpty(),
        passwordInicial: String = "",
        rolInicial: Int = usuario?.idRol ?: UsuarioAdmin.ROL_ALUMNO,
        activoInicial: Boolean = usuario?.estado != 0
    ) {
        usuarioEditado = usuario
        ++generacion
        panel.removeAllViews()
        bottomActions()
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE
        val esNuevo = usuario == null
        marco.tituloEncabezado.setText(
            if (esNuevo) R.string.titulo_crear_registro else R.string.titulo_editar_registro
        )

        val vista = FormAdminUsuarioBinding.inflate(layoutInflater, panel, true)
        vista.campoContrasena.hint = getString(
            if (esNuevo) R.string.campo_nueva_contrasena else R.string.campo_editar_contrasena
        )
        vista.entradaNombre.setText(nombreInicial)
        vista.entradaApellido.setText(apellidoInicial)
        vista.entradaUsuario.setText(usuarioInicial)
        vista.entradaContrasena.setText(passwordInicial)
        vista.selectorRol.setSimpleItems(
            arrayOf(getString(R.string.rol_alumno), getString(R.string.rol_administrador))
        )
        vista.selectorRol.setText(
            getString(
                if (rolInicial == UsuarioAdmin.ROL_ADMIN) R.string.rol_administrador
                else R.string.rol_alumno
            ),
            false
        )
        vista.interruptorActivo.isChecked = activoInicial
        vista.entradaNombre.doAfterTextChanged { vista.campoNombre.error = null }
        vista.entradaApellido.doAfterTextChanged { vista.campoApellido.error = null }
        vista.entradaUsuario.doAfterTextChanged { vista.campoUsuario.error = null }
        formulario = vista

        if (usuario != null) {
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

        val apellido = vista.entradaApellido.text?.toString()?.trim().orEmpty()
        if (apellido.isBlank()) {
            vista.campoApellido.error = getString(R.string.error_apellido_requerido)
            return
        }
        vista.campoApellido.error = null

        val usuario = vista.entradaUsuario.text?.toString()?.trim().orEmpty()
        if (usuario.isBlank()) {
            vista.campoUsuario.error = getString(R.string.error_usuario_requerido)
            return
        }
        vista.campoUsuario.error = null

        val password = vista.entradaContrasena.text?.toString().orEmpty()
        val esNuevo = usuarioEditado == null
        if ((esNuevo || password.isNotEmpty()) && password.length < 8) {
            vista.campoContrasena.error = getString(R.string.error_contrasena_corta)
            return
        }
        vista.campoContrasena.error = null

        val datos = UsuarioAdmin(
            id = usuarioEditado?.id,
            idRol = rolSeleccionado(vista.selectorRol.text?.toString()),
            nombre = nombre,
            apellido = apellido,
            usuario = usuario,
            password = password.ifEmpty { null },
            estado = if (vista.interruptorActivo.isChecked) 1 else 0
        )
        val llamada =
            if (esNuevo) RetrofitClient.api.usuarioCrear(datos)
            else RetrofitClient.api.usuarioActualizar(datos)
        peticion(llamada) { cargar() }
    }

    private fun rolSeleccionado(texto: String?): Int =
        if (texto == getString(R.string.rol_administrador)) UsuarioAdmin.ROL_ADMIN
        else UsuarioAdmin.ROL_ALUMNO

    private fun confirmarDesactivar() {
        val id = usuarioEditado?.id ?: return
        AlertDialog.Builder(this)
            .setTitle(R.string.accion_desactivar_registro)
            .setMessage(R.string.confirmacion_desactivar_registro)
            .setNegativeButton(R.string.accion_cancelar, null)
            .setPositiveButton(R.string.accion_desactivar) { _, _ ->
                peticion(RetrofitClient.api.usuarioDesactivar(IdUsuario(id))) { cargar() }
            }
            .show()
    }
}

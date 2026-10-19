package com.example.four_ways_learn.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.four_ways_learn.MainActivity
import com.example.four_ways_learn.R
import com.example.four_ways_learn.admin.AdminActivity
import com.example.four_ways_learn.admin.AdminPreguntasActivity
import com.example.four_ways_learn.admin.AdminCursosActivity
import com.example.four_ways_learn.admin.AdminTemasActivity
import com.example.four_ways_learn.admin.AdminLeccionesActivity
import com.example.four_ways_learn.admin.AdminUsuariosActivity
import com.example.four_ways_learn.admin.AdminResultadosActivity
import com.example.four_ways_learn.admin.AdminContentActivity
import com.example.four_ways_learn.alumno.*
import com.example.four_ways_learn.databinding.ActivityShellBinding
import com.example.four_ways_learn.databinding.ItemProgressCardBinding
import com.example.four_ways_learn.databinding.ItemLessonRowBinding
import com.example.four_ways_learn.model.Leccion
import com.example.four_ways_learn.network.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import kotlin.math.roundToInt
import com.google.firebase.auth.FirebaseAuth


open class BaseActivity : AppCompatActivity() {
    protected open val requiereSesion = true
    protected open val requiereAdmin = false
    private val llamadas = mutableListOf<Call<*>>()
    private var peticionesPendientes = 0
    private var seleccionandoNavegacion = false
    protected lateinit var marco: ActivityShellBinding
    protected lateinit var panel: LinearLayout
    protected lateinit var contenidoLista: LinearLayout
    protected lateinit var mensajeEstado: TextView
    protected lateinit var progreso: ProgressBar
    protected lateinit var reintentar: MaterialButton
    protected val esAdmin get() = SessionStore.usuario?.rol.equals("ADMIN", ignoreCase = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStore.init(this)
        if (requiereSesion && SessionStore.token == null) irALogin()
        else if (requiereAdmin && !esAdmin) finish()
    }

    override fun onResume() {
        super.onResume()
        if (requiereSesion && SessionStore.token == null) irALogin()
        else if (requiereAdmin && !esAdmin) finish()
        if (::marco.isInitialized && requiereSesion) seleccionarNavegacion()
    }

    protected fun dp(valor: Int) = (valor * resources.displayMetrics.density).roundToInt()
    protected fun dim(id: Int) = resources.getDimensionPixelSize(id)
    protected fun color(id: Int) = ContextCompat.getColor(this, id)
    protected fun pantalla(titulo: String, back: Boolean = true) {
        marco = ActivityShellBinding.inflate(layoutInflater)
        setContentView(marco.root)
        panel = marco.panelContenido; mensajeEstado = marco.textoEstado; progreso = marco.cargando; reintentar =
            marco.botonReintentar
        contenidoLista = marco.contenidoLista
        marco.tituloEncabezado.text = titulo
        marco.botonAtras.visibility = if (back) View.VISIBLE else View.GONE
        marco.botonAtras.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        marco.iconoMarca.visibility = if (back) View.GONE else View.VISIBLE
        marco.insigniaAdmin.visibility = if (esAdmin) View.VISIBLE else View.GONE
        marco.botonCuenta.text = SessionStore.usuario?.nombre?.take(1)?.uppercase().orEmpty()
        marco.botonCuenta.setOnClickListener { navegar(AccountActivity::class.java) }
        marco.botonCuenta.visibility = if (requiereSesion) View.VISIBLE else View.GONE
        if (!requiereSesion) marco.encabezado.visibility = View.GONE
        configurarNavegacion()
        marco.scrollContenido.isVerticalScrollBarEnabled = false
        marco.scrollContenido.isHorizontalScrollBarEnabled = false
        ViewCompat.setOnApplyWindowInsetsListener(marco.root) { view, insets ->
            val bars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            marco.navegacionInferior.visibility =
                if (requiereSesion && !insets.isVisible(WindowInsetsCompat.Type.ime())) View.VISIBLE else View.GONE
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(marco.root)
    }

    protected fun mostrarLista() {
        marco.scrollContenido.visibility = View.GONE
        marco.contenidoLista.visibility = View.VISIBLE
    }

    protected fun mostrarFormulario() {
        marco.scrollContenido.visibility = View.VISIBLE
        marco.contenidoLista.visibility = View.GONE
    }

    private fun seleccionarNavegacion() {
        seleccionandoNavegacion = true
        marco.navegacionInferior.selectedItemId = when {
            this is AccountActivity || this is MisMensajesActivity -> R.id.nav_cuenta
            this is AdminContentActivity || this is AdminCursosActivity ||
                this is AdminTemasActivity || this is AdminUsuariosActivity ||
                this is AdminLeccionesActivity || this is AdminPreguntasActivity -> R.id.nav_contenido
            this is AdminResultadosActivity -> R.id.nav_resultados

            esAdmin -> R.id.nav_panel
            this is ProgresoActivity -> R.id.nav_progreso
            this is BienvenidaActivity -> R.id.nav_inicio
            else -> R.id.nav_cursos
        }
        seleccionandoNavegacion = false
    }

    private fun configurarNavegacion() {
        val nav = marco.navegacionInferior
        if (!requiereSesion) {
            nav.visibility = View.GONE; return
        }
        nav.inflateMenu(if (esAdmin) R.menu.navegacion_admin else R.menu.navegacion_alumno)
        seleccionarNavegacion()
        nav.setOnItemSelectedListener { elemento ->
            if (seleccionandoNavegacion) return@setOnItemSelectedListener true
            abrirNavegacion(elemento.itemId)
            true
        }
        nav.setOnItemReselectedListener { elemento ->
            if (!seleccionandoNavegacion) abrirNavegacion(elemento.itemId)
        }
    }

    private fun abrirNavegacion(id: Int) {
        when (id) {
            R.id.nav_inicio -> navegar(BienvenidaActivity::class.java)
            R.id.nav_cursos -> navegar(CursosActivity::class.java)
            R.id.nav_progreso -> if (!esAdmin) navegar(ProgresoActivity::class.java)
            R.id.nav_panel -> navegar(AdminActivity::class.java)
            R.id.nav_contenido -> if (esAdmin) navegar(AdminContentActivity::class.java)
            R.id.nav_resultados -> navegar(AdminResultadosActivity::class.java)
            R.id.nav_cuenta -> navegar(AccountActivity::class.java)
        }
    }

    protected fun navegar(target: Class<out AppCompatActivity>) {
        if (javaClass != target) startActivity(
            Intent(
                this,
                target
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    protected fun text(valor: String, size: Int = 17, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = valor; textSize = size.toFloat(); setTextColor(color(R.color.fw_tinta))
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setLineSpacing(dp(3).toFloat(), 1.05f); setPadding(0, dp(4), 0, dp(8))
        }

    protected fun atenuado(valor: String, size: Int = 15) =
        text(valor, size).apply { setTextColor(color(R.color.fw_apagado)) }

    protected fun boton(valor: String, alClic: () -> Unit): MaterialButton =
        MaterialButton(this).apply {
            text = valor; isAllCaps = false; minHeight = dp(54); cornerRadius = dp(14)
            layoutParams =
                LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8); bottomMargin = dp(8) }
            setOnClickListener { alClic() }
        }

    protected fun secundario(valor: String, alClic: () -> Unit) = boton(valor, alClic).apply {
        backgroundTintList =
            ColorStateList.valueOf(color(R.color.fw_azul_suave)); setTextColor(color(R.color.fw_azul))
    }

    protected fun contorno(valor: String, alClic: () -> Unit) = boton(valor, alClic).apply {
        backgroundTintList = ColorStateList.valueOf(color(R.color.fw_tarjeta))
        setTextColor(color(R.color.fw_azul)); strokeColor =
        ColorStateList.valueOf(color(R.color.fw_azul)); strokeWidth = dp(1)
    }

    protected fun bottomActions(vararg actions: MaterialButton) {
        marco.accionesInferiores.removeAllViews()
        marco.accionesInferiores.visibility = if (actions.isEmpty()) View.GONE else View.VISIBLE
        actions.forEachIndexed { indice, action ->
            action.maxLines = 2
            action.setPadding(dp(10), dp(12), dp(10), dp(12))
            marco.accionesInferiores.addView(action, LinearLayout.LayoutParams(0, -2, 1f).apply {
                if (indice > 0) marginStart = dp(10)
            })
        }
    }

    protected fun caja(
        contenedor: LinearLayout = panel,
        tone: Int = R.color.fw_tarjeta,
        padding: Int = 18,
        block: (LinearLayout) -> Unit
    ): MaterialCardView {
        val tarjeta = MaterialCardView(this).apply {
            radius = dp(20).toFloat(); cardElevation = 0f; setCardBackgroundColor(color(tone))
            strokeColor = color(R.color.fw_borde); strokeWidth =
            if (tone == R.color.fw_tarjeta) dp(1) else 0
        }
        val inside = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(
            dp(padding),
            dp(padding),
            dp(padding),
            dp(padding)
        )
        }
        tarjeta.addView(inside); block(inside)
        contenedor.addView(tarjeta, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) })
        return tarjeta
    }

    protected fun icono(id: Int, size: Int = 44, tone: Int = R.color.fw_azul_suave) =
        ImageView(this).apply {
            setImageResource(id); imageTintList = ColorStateList.valueOf(color(R.color.fw_azul))
            background = redondeado(tone, 12); setPadding(dp(10), dp(10), dp(10), dp(10))
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            layoutParams = LinearLayout.LayoutParams(dp(size), dp(size))
        }

    protected fun redondeado(tone: Int, radius: Int = 12) =
        GradientDrawable().apply { cornerRadius = dp(radius).toFloat(); setColor(color(tone)) }

    protected fun iconoCurso(name: String): Int = when {
        name.contains("datos", true) -> R.drawable.icono_base_datos
        name.contains("web", true) -> R.drawable.icono_web
        name.contains("algoritmo", true) -> R.drawable.icono_cuadricula
        else -> R.drawable.icono_codigo
    }

    protected fun porcentajeTexto(valor: Double): String = if (valor % 1.0 == 0.0) getString(
        R.string.porcentaje_entero,
        valor.toInt()
    ) else getString(R.string.porcentaje_decimal, valor)

    protected fun barraProgreso(valor: Double): LinearProgressIndicator =
        LinearProgressIndicator(this).apply {
            isIndeterminate = false; max = 10000; trackThickness = dp(7); trackCornerRadius = dp(4)
            setIndicatorColor(color(R.color.fw_azul)); trackColor = color(R.color.fw_pista)
            setProgressCompat((valor.coerceIn(0.0, 100.0) * 100).roundToInt(), false)
            contentDescription = getString(R.string.accesibilidad_progreso, porcentajeTexto(valor))
            layoutParams = LinearLayout.LayoutParams(-1, dp(12))
                .apply { topMargin = dp(8); bottomMargin = dp(8) }
        }

    protected fun actualizarProgreso(bar: LinearProgressIndicator, valor: Double) {
        bar.setProgressCompat((valor.coerceIn(0.0, 100.0) * 100).roundToInt(), false)
        bar.contentDescription = getString(R.string.accesibilidad_progreso, porcentajeTexto(valor))
    }

    protected fun insignia(valor: String): TextView = text(valor, 12, true).apply {
        setPadding(
            dp(10),
            dp(5),
            dp(10),
            dp(5)
        ); setTextColor(color(R.color.fw_verde)); background = redondeado(R.color.fw_menta)
        layoutParams = LinearLayout.LayoutParams(-2, -2).apply { bottomMargin = dp(8) }
    }

    protected fun estadoProgreso(completadas: Int, total: Int): String = when {
        total > 0 && completadas == total -> getString(R.string.progreso_completado)
        completadas > 0 -> getString(R.string.progreso_en_curso)
        else -> getString(R.string.progreso_no_iniciado)
    }

    protected fun tarjetaProgreso(
        titulo: String,
        detalle: String,
        valor: Double,
        leyenda: String,
        estadoActual: String,
        contenedor: LinearLayout = panel,
        iconRes: Int = R.drawable.icono_libro,
        alClic: () -> Unit
    ) {
        val tarjeta = ItemProgressCardBinding.inflate(layoutInflater, contenedor, true)
        tarjeta.tituloCurso.text = titulo; tarjeta.detalleCurso.text = detalle
        tarjeta.detalleCurso.visibility = if (detalle.isBlank()) View.GONE else View.VISIBLE
        tarjeta.iconoTarjeta.setImageResource(iconRes); tarjeta.etiquetaPorcentaje.text = porcentajeTexto(valor)
        tarjeta.leyendaCurso.text = leyenda; tarjeta.estadoCurso.text = estadoActual
        actualizarProgreso(tarjeta.progresoCurso, valor)
        tarjeta.root.setOnClickListener { alClic() }
    }

    protected fun filaLeccion(leccion: Leccion, contenedor: LinearLayout, alClic: () -> Unit) {
        val fila = ItemLessonRowBinding.inflate(layoutInflater, contenedor, true)
        fila.tituloLeccion.text = leccion.titulo
        val completadas = leccion.completada == 1
        fila.estadoLeccion.text =
            getString(if (completadas) R.string.estado_completada else R.string.estado_pendiente)
        fila.estadoLeccion.setTextColor(color(if (completadas) R.color.fw_verde else R.color.fw_apagado))
        if (!completadas) {
            fila.iconoEstadoLeccion.setImageDrawable(null)
            fila.iconoEstadoLeccion.setBackgroundResource(R.drawable.fondo_pendiente)
        }
        fila.root.setOnClickListener { alClic() }
    }

    protected fun resumenProgreso(titulo: String, completadas: Int, total: Int, valor: Double) {
        caja(tone = R.color.fw_marino, padding = 22) { inside ->
            inside.addView(text(titulo, 17, true).apply { setTextColor(color(R.color.fw_sobre_marino)) })
            inside.addView(
                text(
                    porcentajeTexto(valor),
                    38,
                    true
                ).apply { setTextColor(color(R.color.fw_sobre_marino)) })
            inside.addView(barraProgreso(valor)); inside.addView(
            text(
                getString(
                    R.string.conteo_lecciones_completadas,
                    completadas,
                    total
                ), 14
            ).apply { setTextColor(color(R.color.fw_sobre_marino)) })
        }
    }

    protected fun campoTexto(
        etiqueta: String,
        valor: String = "",
        tipo: Int = InputType.TYPE_CLASS_TEXT,
        contenedor: LinearLayout = panel
    ): TextInputEditText {
        val layout = TextInputLayout(this).apply {
            hint = etiqueta; boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        }
        val edit = TextInputEditText(layout.context).apply {
            inputType = tipo; setText(valor); textSize = 16f; setTextColor(color(R.color.fw_tinta))
            if ((tipo and InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) isSingleLine = true
        }
        if ((tipo and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD) layout.endIconMode =
            TextInputLayout.END_ICON_PASSWORD_TOGGLE
        layout.addView(edit, LinearLayout.LayoutParams(-1, -2))
        contenedor.addView(
            layout,
            LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dim(R.dimen.espacio_14) }
        )
        return edit
    }

    protected fun mensaje(valor: String) {
        mensajeEstado.text = valor; mensajeEstado.visibility = View.VISIBLE
    }

    protected fun <T> peticion(
        call: Call<ApiResponse<T>>,
        alErrorConexion: (() -> Unit)? = null,
        alExito: (T?) -> Unit
    ) {
        if (requiereSesion && SessionStore.token == null) {
            return
        }

        if (requiereAdmin && !esAdmin) {
            return
        }

        if (isFinishing || isDestroyed) {
            return
        }

        peticionesPendientes++
        llamadas.add(call)

        progreso.visibility = View.VISIBLE
        mensajeEstado.visibility = View.GONE
        reintentar.visibility = View.GONE

        ApiRepository.execute(
            call = call,
            success = { data ->
                if (!isFinishing && !isDestroyed) {
                    finalizarPeticion(call)
                    alExito(data)
                }
            },
            alFallo = { error ->
                if (!isFinishing && !isDestroyed) {
                    finalizarPeticion(call)

                    if (error.mensajeEstado == 401) {
                        SessionStore.clear()
                    }

                    when {
                        error.mensajeEstado == 401 && requiereSesion -> {
                            irALogin()
                        }

                        error.esErrorConexion && alErrorConexion != null -> {
                            alErrorConexion()
                        }

                        else -> {
                            mensaje(error.localizedMessage(this))

                            reintentar.visibility = View.VISIBLE
                            reintentar.setOnClickListener {
                                peticion(
                                    call = call.clone(),
                                    alErrorConexion = alErrorConexion,
                                    alExito = alExito
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    private fun finalizarPeticion(call: Call<*>) {
        llamadas.remove(call)

        if (peticionesPendientes > 0) {
            peticionesPendientes--
        }

        if (peticionesPendientes > 0) {
            progreso.visibility = View.VISIBLE
        } else {
            progreso.visibility = View.GONE
        }
    }

    protected fun cerrarSesion() {
        peticion(RetrofitClient.api.cerrarSesion()){
            FirebaseAuth.getInstance().signOut()
            SessionStore.clear()
            irALogin()
        }
    }

    protected fun irALogin() {
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ); finish()
    }

    override fun onDestroy() {
        llamadas.toList().forEach { it.cancel() }; super.onDestroy()
    }

}

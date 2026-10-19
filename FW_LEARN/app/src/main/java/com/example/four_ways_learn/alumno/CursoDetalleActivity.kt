package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityCursoDetalleBinding
import com.example.four_ways_learn.model.Leccion
import com.example.four_ways_learn.model.Topico
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity
import android.util.Log
import com.example.four_ways_learn.local.TopicoStore
import com.example.four_ways_learn.network.SessionStore

class CursoDetalleActivity : BaseActivity() {
    private val expandidos = mutableSetOf<Int>()
    private var expansionInicializada = false
    private lateinit var content: ActivityCursoDetalleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.titulo_detalle_curso))
        content = ActivityCursoDetalleBinding.inflate(layoutInflater, panel, true)
        savedInstanceState?.getIntArray("expanded_topics")
            ?.let { expandidos.addAll(it.toList()); expansionInicializada = true }
        val name = intent.getStringExtra("cursoNombre") ?: getString(R.string.tu_curso)
        content.nombreCurso.text = name; content.iconoPrincipal.setImageResource(iconoCurso(name))
        content.descripcionCurso.text = intent.getStringExtra("cursoDescripcion").orEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray("expanded_topics", expandidos.toIntArray()); super.onSaveInstanceState(
            outState
        )
    }

    override fun onResume() {
        super.onResume(); cargar()
    }

    private fun cargar() {
        val idCurso = intent.getIntExtra("cursoId", -1)

        if (idCurso < 1) {
            mensaje(getString(R.string.error_curso_faltante))
            return
        }

        peticion(
            RetrofitClient.api.topicos(idCurso),
            alErrorConexion = {
                cargarTemasGuardados(idCurso)
            }
        ) { temas ->
            if (temas != null) {
                guardarYMostrarTemas(idCurso, temas)
            } else {
                mostrarTemas(null, consultarServidor = true)
            }
        }
    }

    private fun guardarYMostrarTemas(
        idCurso: Int,
        temas: List<Topico>
    ) {
        val usuario = SessionStore.usuario

        if (usuario == null) {
            mostrarTemas(temas, consultarServidor = true)
            return
        }

        val idUsuario = usuario.id_usuario
        val contexto = applicationContext

        Thread {
            try {
                val temasGuardados = TopicoStore(contexto).use { store ->
                    store.guardarTopicos(idUsuario, idCurso, temas)
                    store.obtenerTopicos(idUsuario, idCurso)
                }

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarTemas(
                            temasGuardados,
                            consultarServidor = true
                        )

                        Log.d(
                            "SQLiteFW",
                            "Temas guardados del curso $idCurso: " +
                                    temasGuardados.size
                        )
                    }
                }
            } catch (error: Exception) {
                Log.e("SQLiteFW", "Error al guardar los temas", error)

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarTemas(temas, consultarServidor = true)
                    }
                }
            }
        }.start()
    }

    private fun cargarTemasGuardados(idCurso: Int) {
        val idUsuario = SessionStore.usuario?.id_usuario ?: return
        val contexto = applicationContext

        Thread {
            try {
                val temasGuardados = TopicoStore(contexto).use { store ->
                    store.obtenerTopicos(idUsuario, idCurso)
                }

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarTemas(
                            temasGuardados,
                            consultarServidor = false
                        )

                        val mensaje = if (temasGuardados.isEmpty()) {
                            R.string.temas_cache_vacio
                        } else {
                            R.string.temas_guardados_mostrar
                        }

                        mensaje(getString(mensaje))

                        reintentar.visibility = View.VISIBLE
                        reintentar.setOnClickListener {
                            cargar()
                        }

                        Log.d(
                            "SQLiteFW",
                            "Temas leídos sin conexión: ${temasGuardados.size}"
                        )
                    }
                }
            } catch (error: Exception) {
                Log.e("SQLiteFW", "Error al leer los temas guardados", error)

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mensaje(getString(R.string.temas_cache_error_lectura))

                        reintentar.visibility = View.VISIBLE
                        reintentar.setOnClickListener {
                            cargar()
                        }
                    }
                }
            }
        }.start()
    }

    private fun mostrarTemas(
        temas: List<Topico>?,
        consultarServidor: Boolean
    ) {
        content.listaTemas.removeAllViews()
        bottomActions()

        var total = 0
        var completadas = 0

        for (tema in temas.orEmpty()) {
            total += tema.total_lecciones
            completadas += tema.lecciones_completadas
        }

        val porcentaje = if (total > 0) {
            completadas * 100.0 / total
        } else {
            0.0
        }

        content.conteoCompletadas.text = getString(
            R.string.conteo_lecciones_completadas,
            completadas,
            total
        )

        content.porcentajeTotal.text = porcentajeTexto(porcentaje)
        actualizarProgreso(content.progresoTotal, porcentaje)

        if (temas.isNullOrEmpty()) {
            mensaje(getString(R.string.temas_vacios))
            return
        }

        if (!expansionInicializada) {
            val siguienteTema = temas.firstOrNull {
                it.total_lecciones > it.lecciones_completadas
            } ?: temas.first()

            expandidos.add(siguienteTema.id)
            expansionInicializada = true
        }

        for (tema in temas) {
            tarjetaTema(tema, consultarServidor)
        }

        bottomActions(
            boton(getString(R.string.accion_continuar_curso)) {
                if (!consultarServidor) {
                    mensaje(getString(R.string.lecciones_requieren_conexion))
                    return@boton
                }

                val siguienteTema = temas.firstOrNull {
                    it.total_lecciones > it.lecciones_completadas
                } ?: temas.firstOrNull {
                    it.total_lecciones > 0
                }

                if (siguienteTema == null) {
                    mensaje(getString(R.string.lecciones_vacias))
                    return@boton
                }

                peticion(RetrofitClient.api.lecciones(siguienteTema.id)) { lecciones ->
                    val leccion = lecciones?.firstOrNull {
                        it.completada != 1
                    } ?: lecciones?.firstOrNull()

                    if (leccion == null) {
                        mensaje(getString(R.string.lecciones_vacias))
                    } else {
                        abrirLeccion(leccion)
                    }
                }
            }
        )
    }

    private fun tarjetaTema(tema: Topico, consultarServidor: Boolean) {
        caja(content.listaTemas, padding = 14) { inside ->
            val titulo = secundario(tema.nombre) {}.apply {
                setIconResource(if (tema.id in expandidos) R.drawable.icono_abajo else R.drawable.icono_flecha)
                textSize = 15f; gravity =
                android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
                contentDescription = getString(R.string.accesibilidad_alternar_tema, tema.nombre)
            }
            inside.addView(titulo)
            inside.addView(
                atenuado(
                    getString(
                        R.string.conteo_lecciones_porcentaje,
                        tema.lecciones_completadas,
                        tema.total_lecciones,
                        porcentajeTexto(tema.porcentaje_avance)
                    ), 12
                )
            )
            inside.addView(barraProgreso(tema.porcentaje_avance))
            val filas = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL; visibility = View.GONE
            }
            inside.addView(filas)
            var cargado = false
            var cargando = false
            fun populate() {
                filas.visibility = View.VISIBLE
                if (cargado || cargando) {
                    return
                }

                if (!consultarServidor){
                    cargado = true
                    filas.removeAllViews()

                    filas.addView(
                        atenuado(getString(R.string.lecciones_requieren_conexion))
                    )

                    return
                }

                cargando = true

                peticion(RetrofitClient.api.lecciones(tema.id)) { lecciones ->
                    cargando = false; cargado = true; filas.removeAllViews()
                    if (lecciones.isNullOrEmpty()) filas.addView(atenuado(getString(R.string.lecciones_vacias)))
                    lecciones.orEmpty()
                        .forEach { leccion -> filaLeccion(leccion, filas) { abrirLeccion(leccion) } }
                    filas.visibility = if (tema.id in expandidos) View.VISIBLE else View.GONE
                }
            }
            titulo.setOnClickListener {
                if (tema.id in expandidos) {
                    expandidos.remove(tema.id); filas.visibility =
                        View.GONE; titulo.setIconResource(R.drawable.icono_flecha)
                } else {
                    expandidos.add(tema.id); titulo.setIconResource(R.drawable.icono_abajo); populate()
                }
            }
            if (tema.id in expandidos) populate()
        }
    }

    private fun abrirLeccion(leccion: Leccion) {
        startActivity(
            Intent(this, AprendizajeActivity::class.java).putExtra(
                "leccionId",
                leccion.id_leccion
            )
        )
    }
}

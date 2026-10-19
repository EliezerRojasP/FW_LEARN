package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityCursosBinding
import com.example.four_ways_learn.databinding.FeaturedLearningBinding
import com.example.four_ways_learn.model.Curso
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.network.SessionStore
import com.example.four_ways_learn.ui.BaseActivity
import android.util.Log
import com.example.four_ways_learn.local.CursoStore
import android.view.View


open class CursosActivity : BaseActivity() {

    private lateinit var binding: ActivityCursosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pantalla(getString(R.string.titulo_app), false)

        binding = ActivityCursosBinding.inflate(
            layoutInflater,
            panel,
            true
        )

        mostrarSaludo()
    }

    override fun onResume() {
        super.onResume()

        if (!isFinishing && !isDestroyed) {
            cargarCursos()
        }
    }

    private fun mostrarSaludo() {
        val nombre = SessionStore.usuario?.nombre
            ?: getString(R.string.rol_alumno)

        binding.saludo.text = getString(
            R.string.saludo_panel,
            nombre
        )

        binding.subtitulo.setText(R.string.titulo_cursos)
    }

    private fun cargarCursos() {
        Log.d("SQLiteFW", "1. Solicitando cursos al servidor")

        peticion(
            RetrofitClient.api.cursos(),
            alErrorConexion = {
                Log.d("SQLiteFW", "2. Falló la conexión; consultando SQLite")
                cargarCursosGuardados()
            }
        ) { cursos ->
            Log.d(
                "SQLiteFW",
                "2. Respondió el servidor; cursos: ${cursos?.size}"
            )

            if (cursos != null) {
                guardarYMostrarCursos(cursos)
            } else {
                mostrarCursos(null)
            }
        }
    }

    private fun guardarYMostrarCursos(cursos: List<Curso>) {
        val usuario = SessionStore.usuario

        if (usuario == null) {
            mostrarCursos(cursos)
            return
        }

        val idUsuario = usuario.id_usuario
        val contexto = applicationContext

        Thread {
            try {
                val cursosGuardados = CursoStore(contexto).use { store ->
                    store.guardarCursos(idUsuario, cursos)
                    store.obtenerCursos(idUsuario)
                }

                Log.d(
                    "SQLiteFW",
                    "Cursos leídos de SQLite: ${cursosGuardados.size}"
                )

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarCursos(cursosGuardados)

                        Log.d(
                            "SQLiteFW",
                            "Pantalla actualizada; hilo: " +
                                    Thread.currentThread().name
                        )
                    }
                }
            } catch (error: Exception) {
                Log.e(
                    "SQLiteFW",
                    "Error al guardar o consultar los cursos",
                    error
                )

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarCursos(cursos)
                    }
                }
            }
        }.start()
    }
    private fun cargarCursosGuardados() {
        val idUsuario = SessionStore.usuario?.id_usuario ?: return
        val contexto = applicationContext

        Thread {
            try {
                val cursosGuardados = CursoStore(contexto).use { store ->
                    store.obtenerCursos(idUsuario)
                }

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarCursos(
                            cursosGuardados,
                            consultarServidor = false
                        )

                        val mensaje = if (cursosGuardados.isEmpty()) {
                            R.string.cursos_cache_vacio
                        } else {
                            R.string.cursos_guardados_mostrar
                        }

                        mensaje(getString(mensaje))

                        reintentar.visibility = View.VISIBLE
                        reintentar.setOnClickListener {
                            cargarCursos()
                        }

                        Log.d(
                            "SQLiteFW",
                            "Cursos mostrados sin conexión: " +
                                    cursosGuardados.size
                        )
                    }
                }
            } catch (error: Exception) {
                Log.e(
                    "SQLiteFW",
                    "Error al leer los cursos guardados",
                    error
                )

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mensaje(getString(R.string.cursos_cache_error_lectura))

                        reintentar.visibility = View.VISIBLE
                        reintentar.setOnClickListener {
                            cargarCursos()
                        }
                    }
                }
            }
        }.start()
    }
    private fun mostrarCursos(cursos: List<Curso>?, consultarServidor: Boolean = true) {
        binding.contenedorDestacado.removeAllViews()
        binding.listaCursos.removeAllViews()

        if (cursos.isNullOrEmpty()) {
            mensaje(getString(R.string.cursos_vacios))
            return
        }

        binding.botonTodosCursos.text = getString(
            R.string.conteo_catalogo,
            cursos.size
        )

        binding.botonTodosCursos.setOnClickListener {
            marco.scrollContenido.smoothScrollTo(
                0,
                binding.root.top + binding.listaCursos.top
            )
        }

        mostrarDestacado(cursos, consultarServidor)
        mostrarListaCursos(cursos)
    }

    private fun mostrarDestacado(cursos: List<Curso>, consultarServidor: Boolean) {
        var totalLecciones = 0
        var leccionesCompletadas = 0

        for (curso in cursos) {
            totalLecciones += curso.total_lecciones
            leccionesCompletadas += curso.lecciones_completadas
        }

        val porcentaje: Double

        if (totalLecciones > 0) {
            porcentaje = 100.0 * leccionesCompletadas / totalLecciones
        } else {
            porcentaje = 0.0
        }

        var cursoDestacado = cursos[0]

        for (curso in cursos) {
            if (curso.total_lecciones > curso.lecciones_completadas) {
                cursoDestacado = curso
                break
            }
        }

        val destacado = FeaturedLearningBinding.inflate(
            layoutInflater,
            binding.contenedorDestacado,
            true
        )

        destacado.tituloDestacado.text = cursoDestacado.nombre
        destacado.cursoDestacado.setText(R.string.destacado_vacio)

        destacado.iconoDestacado.setImageResource(
            iconoCurso(cursoDestacado.nombre)
        )

        destacado.conteoDestacado.text = getString(
            R.string.conteo_lecciones_porcentaje,
            leccionesCompletadas,
            totalLecciones,
            porcentajeTexto(porcentaje)
        )

        actualizarProgreso(destacado.progresoDestacado, porcentaje)

        destacado.botonContinuar.setOnClickListener {
            abrirCurso(cursoDestacado)
        }

        if (consultarServidor) {
            cargarTemaDestacado(cursoDestacado, destacado)
        }
    }

    private fun cargarTemaDestacado(
        curso: Curso,
        destacado: FeaturedLearningBinding
    ) {
        peticion(RetrofitClient.api.topicos(curso.id)) { temas ->
            if (temas.isNullOrEmpty()) {
                return@peticion
            }

            val temaPendiente = temas.firstOrNull {
                it.total_lecciones > it.lecciones_completadas
            }

            val tema = temaPendiente ?: temas.firstOrNull {
                it.total_lecciones > 0
            }

            if (tema != null) {
                cargarLeccionDestacada(curso, tema.id, destacado)
            }
        }
    }

    private fun cargarLeccionDestacada(
        curso: Curso,
        idTema: Int,
        destacado: FeaturedLearningBinding
    ) {
        peticion(RetrofitClient.api.lecciones(idTema)) { lecciones ->
            if (lecciones.isNullOrEmpty()) {
                return@peticion
            }

            val leccionPendiente = lecciones.firstOrNull {
                it.completada != 1
            }

            val leccion = leccionPendiente ?: lecciones[0]

            destacado.tituloDestacado.text = leccion.titulo
            destacado.cursoDestacado.text = curso.nombre

            destacado.botonContinuar.setOnClickListener {
                abrirLeccion(leccion.id_leccion)
            }
        }
    }

    private fun mostrarListaCursos(cursos: List<Curso>) {
        for (curso in cursos) {
            val detalleAvance = getString(
                R.string.conteo_lecciones_completadas,
                curso.lecciones_completadas,
                curso.total_lecciones
            )

            val estadoAvance = estadoProgreso(
                curso.lecciones_completadas,
                curso.total_lecciones
            )

            tarjetaProgreso(
                curso.nombre,
                curso.descripcion,
                curso.porcentaje_avance,
                detalleAvance,
                estadoAvance,
                binding.listaCursos,
                iconoCurso(curso.nombre)
            ) {
                abrirCurso(curso)
            }
        }
    }

    private fun abrirCurso(curso: Curso) {
        val destino = Intent(
            this,
            CursoDetalleActivity::class.java
        )

        destino.putExtra("cursoId", curso.id)
        destino.putExtra("cursoNombre", curso.nombre)
        destino.putExtra("cursoDescripcion", curso.descripcion)

        startActivity(destino)
    }

    private fun abrirLeccion(idLeccion: Int) {
        val destino = Intent(
            this,
            AprendizajeActivity::class.java
        )

        destino.putExtra("leccionId", idLeccion)

        startActivity(destino)
    }
}
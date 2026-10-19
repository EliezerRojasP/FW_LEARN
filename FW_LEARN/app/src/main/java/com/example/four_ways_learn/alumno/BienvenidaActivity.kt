package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityBienvenidaBinding
import com.example.four_ways_learn.databinding.FeaturedLearningBinding
import com.example.four_ways_learn.local.CursoStore
import com.example.four_ways_learn.model.Curso
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.network.SessionStore
import com.example.four_ways_learn.ui.BaseActivity

class BienvenidaActivity : BaseActivity() {

    private lateinit var binding: ActivityBienvenidaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pantalla(getString(R.string.titulo_app), false)

        binding = ActivityBienvenidaBinding.inflate(
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

        binding.saludo.text = getString(R.string.saludo_panel, nombre)
        binding.subtitulo.setText(R.string.subtitulo_panel)
    }

    private fun cargarCursos() {
        peticion(
            RetrofitClient.api.cursos(),
            alErrorConexion = { cargarCursosGuardados() }
        ) { cursos ->
            mostrarResumen(cursos)
        }
    }

    private fun cargarCursosGuardados() {
        val idUsuario = SessionStore.usuario?.id_usuario ?: return
        val contexto = applicationContext

        Thread {
            try {
                val guardados = CursoStore(contexto).use { it.obtenerCursos(idUsuario) }

                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        mostrarResumen(guardados)
                    }
                }
            } catch (error: Exception) {
                Log.e("Bienvenida", "Error al leer cursos guardados", error)
            }
        }.start()
    }

    private fun mostrarResumen(cursos: List<Curso>?) {
        binding.contenedorDestacado.removeAllViews()
        binding.listaPendientes.removeAllViews()

        if (cursos.isNullOrEmpty()) {
            mensaje(getString(R.string.cursos_vacios))
            return
        }

        var totalLecciones = 0
        var completadas = 0

        for (curso in cursos) {
            totalLecciones += curso.total_lecciones
            completadas += curso.lecciones_completadas
        }

        val porcentaje = if (totalLecciones > 0) {
            100.0 * completadas / totalLecciones
        } else {
            0.0
        }

        binding.progresoEstadistica.text = getString(R.string.estadistica_inicio_progreso, porcentajeTexto(porcentaje))
        binding.estadisticaLecciones.text = getString(R.string.estadistica_inicio_lecciones, completadas)
        binding.estadisticaCursos.text = getString(R.string.estadistica_inicio_cursos, cursos.size)

        val cursoDestacado = cursos.firstOrNull {
            it.total_lecciones > it.lecciones_completadas
        } ?: cursos[0]

        mostrarDestacado(cursoDestacado)

        val pendientes = cursos
            .filter { it.id != cursoDestacado.id && it.total_lecciones > it.lecciones_completadas }
            .take(2)

        if (pendientes.isEmpty()) {
            binding.tituloPendientes.visibility = android.view.View.GONE
        } else {
            binding.tituloPendientes.visibility = android.view.View.VISIBLE
            pendientes.forEach { mostrarCursoPendiente(it) }
        }
    }

    private fun mostrarDestacado(curso: Curso) {
        val destacado = FeaturedLearningBinding.inflate(
            layoutInflater,
            binding.contenedorDestacado,
            true
        )

        val porcentaje = if (curso.total_lecciones > 0) {
            100.0 * curso.lecciones_completadas / curso.total_lecciones
        } else {
            0.0
        }

        destacado.tituloDestacado.text = curso.nombre
        destacado.cursoDestacado.setText(R.string.destacado_vacio)
        destacado.iconoDestacado.setImageResource(iconoCurso(curso.nombre))

        destacado.conteoDestacado.text = getString(
            R.string.conteo_lecciones_porcentaje,
            curso.lecciones_completadas,
            curso.total_lecciones,
            porcentajeTexto(porcentaje)
        )

        actualizarProgreso(destacado.progresoDestacado, porcentaje)

        destacado.botonContinuar.setOnClickListener { abrirCurso(curso) }
    }

    private fun mostrarCursoPendiente(curso: Curso) {
        val detalle = getString(
            R.string.conteo_lecciones_completadas,
            curso.lecciones_completadas,
            curso.total_lecciones
        )

        val estado = estadoProgreso(
            curso.lecciones_completadas,
            curso.total_lecciones
        )

        tarjetaProgreso(
            curso.nombre,
            curso.descripcion,
            curso.porcentaje_avance,
            detalle,
            estado,
            binding.listaPendientes,
            iconoCurso(curso.nombre)
        ) {
            abrirCurso(curso)
        }
    }

    private fun abrirCurso(curso: Curso) {
        val destino = Intent(this, CursoDetalleActivity::class.java)

        destino.putExtra("cursoId", curso.id)
        destino.putExtra("cursoNombre", curso.nombre)
        destino.putExtra("cursoDescripcion", curso.descripcion)

        startActivity(destino)
    }
}
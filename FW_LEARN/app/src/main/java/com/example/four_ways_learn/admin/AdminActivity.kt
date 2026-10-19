package com.example.four_ways_learn.admin

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityAdminBinding
import com.example.four_ways_learn.databinding.ItemStatCardBinding
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.ui.BaseActivity

class AdminActivity : BaseActivity() {
    override val requiereAdmin = true
    private lateinit var content: ActivityAdminBinding
    private lateinit var courseStat: ItemStatCardBinding
    private lateinit var lessonStat: ItemStatCardBinding
    private lateinit var questionStat: ItemStatCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla(getString(R.string.titulo_app), false)
        content = ActivityAdminBinding.inflate(layoutInflater, panel, true)
        fun estadistica(etiqueta: Int, icono: Int): ItemStatCardBinding {
            val tarjeta = ItemStatCardBinding.inflate(layoutInflater, content.filaEstadisticas, false)
            tarjeta.etiquetaEstadistica.setText(etiqueta); tarjeta.iconoEstadistica.setImageResource(icono)
            tarjeta.numeroEstadistica.text = getString(R.string.marcador_estadistica)
            content.filaEstadisticas.addView(
                tarjeta.root,
                LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(6) })
            return tarjeta
        }
        courseStat = estadistica(R.string.modulo_cursos, R.drawable.icono_libro)
        lessonStat = estadistica(R.string.modulo_lecciones, R.drawable.icono_leccion)
        questionStat = estadistica(R.string.preguntas_corto, R.drawable.icono_pregunta)
        content.tarjetaCursos.setOnClickListener {
            startActivity(Intent(this, AdminCursosActivity::class.java))
        }
        content.tarjetaTemas.setOnClickListener {
            startActivity(Intent(this, AdminTemasActivity::class.java))
        }
        content.tarjetaLecciones.setOnClickListener {
            startActivity(Intent(this, AdminLeccionesActivity::class.java))
        }
        content.tarjetaUsuarios.setOnClickListener {
            startActivity(Intent(this, AdminUsuariosActivity::class.java))
        }
        content.tarjetaResultados.setOnClickListener {
            startActivity(Intent(this, AdminResultadosActivity::class.java))
        }
        content.tarjetaEstadisticas.setOnClickListener {
            startActivity(Intent(this, AdminEstadisticasActivity::class.java))
        }
        content.tarjetaPreguntas.setOnClickListener {
            startActivity(Intent(this, AdminPreguntasActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        peticion(RetrofitClient.api.estadisticasAdmin()) { stats ->
            if (stats == null) return@peticion
            courseStat.numeroEstadistica.text = getString(R.string.estadistica_entero, stats.cursos)
            lessonStat.numeroEstadistica.text = getString(R.string.estadistica_entero, stats.lecciones)
        }
        peticion(RetrofitClient.api.preguntasAdmin()) { preguntas ->
            val count = preguntas.orEmpty().count { it.estado == 1 }
            questionStat.numeroEstadistica.text = getString(R.string.estadistica_entero, count)
        }
    }
}

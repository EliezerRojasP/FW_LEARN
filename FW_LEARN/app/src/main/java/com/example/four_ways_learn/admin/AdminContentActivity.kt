package com.example.four_ways_learn.admin

import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.R
import com.example.four_ways_learn.databinding.ActivityAdminContentBinding
import com.example.four_ways_learn.ui.BaseActivity

class AdminContentActivity : BaseActivity() {
    override val requiereAdmin = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.nav_contenido), false)
        val content = ActivityAdminContentBinding.inflate(layoutInflater, panel, true)
        content.tarjetaCursos.setOnClickListener {
            startActivity(Intent(this, AdminCursosActivity::class.java))
        }
        content.tarjetaTemas.setOnClickListener {
            startActivity(Intent(this, AdminTemasActivity::class.java))
        }
        content.tarjetaLecciones.setOnClickListener {
            startActivity(Intent(this, AdminLeccionesActivity::class.java))
        }
        content.tarjetaPreguntas.setOnClickListener {
            startActivity(Intent(this, AdminPreguntasActivity::class.java))
        }
    }
}

package com.example.four_ways_learn.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.four_ways_learn.R
import com.example.four_ways_learn.model.MensajeAlumno
import com.example.four_ways_learn.network.ApiResponse
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.network.SessionStore
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : BaseActivity() {

    private var botonMisMensajes: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.titulo_cuenta))
        val usuario = SessionStore.usuario ?: return
        panel.addView(text(getString(R.string.titulo_cuenta), 28, true))
        panel.addView(atenuado(getString(R.string.descripcion_cuenta)))
        caja { inside ->
            inside.addView(icono(R.drawable.icono_usuario, 64))
            inside.addView(text(getString(R.string.identidad_usuario_admin, usuario.nombre, usuario.apellido), 22, true))
            inside.addView(atenuado(getString(R.string.usuario_cuenta, usuario.usuario)))
            inside.addView(insignia(getString(R.string.rol_cuenta, getString(if (esAdmin) R.string.rol_administrador else R.string.rol_alumno))))
        }
        panel.addView(atenuado(getString(if (esAdmin) R.string.ayuda_cuenta else R.string.ayuda_estudiante_cuenta), 14))

        if (!esAdmin) {
            panel.addView(boton("Comunicarse con el administrador") {
                DialogoMensajeAdmin.mostrar(this@AccountActivity)
            })

            val colorPrimario = MaterialColors.getColor(
                this, androidx.appcompat.R.attr.colorPrimary, Color.BLUE
            )
            val misMensajes = boton("Mis mensajes") {
                startActivity(Intent(this@AccountActivity, MisMensajesActivity::class.java))
            }.apply {
                backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                setTextColor(colorPrimario)
                strokeColor = ColorStateList.valueOf(colorPrimario)
                strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
            botonMisMensajes = misMensajes
            panel.addView(misMensajes)
        }

        panel.addView(boton(getString(R.string.accion_cerrar_sesion)) { cerrarSesion() }.apply {
            setIconResource(R.drawable.icono_salir)
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this@AccountActivity, R.color.fw_peligro)
            )
            (layoutParams as? LinearLayout.LayoutParams)?.topMargin =
                (32 * resources.displayMetrics.density).toInt()
        })
    }

    override fun onResume() {
        super.onResume()
        if (!esAdmin) actualizarAviso()
    }

    private fun actualizarAviso() {
        val boton = botonMisMensajes ?: return
        RetrofitClient.api.misMensajes()
            .enqueue(object : Callback<ApiResponse<List<MensajeAlumno>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<MensajeAlumno>>>,
                    response: Response<ApiResponse<List<MensajeAlumno>>>
                ) {
                    if (isFinishing || isDestroyed) return
                    val nuevas = response.body()?.data.orEmpty()
                        .count { !it.respuesta.isNullOrBlank() && it.respuesta_vista == 0 }
                    boton.text = when (nuevas) {
                        0 -> "Mis mensajes"
                        1 -> "Mis mensajes (1 nueva)"
                        else -> "Mis mensajes ($nuevas nuevas)"
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<MensajeAlumno>>>, t: Throwable) {}
            })
    }
}
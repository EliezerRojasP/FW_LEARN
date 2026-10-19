package com.example.four_ways_learn.ui

import android.os.Bundle
import android.widget.Toast
import com.example.four_ways_learn.model.MensajeAlumno
import com.example.four_ways_learn.model.MensajeVistoRequest
import com.example.four_ways_learn.network.ApiResponse
import com.example.four_ways_learn.network.RetrofitClient
import com.example.four_ways_learn.network.SessionStore
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisMensajesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantalla("Mis mensajes")
        if (SessionStore.usuario == null) return

        panel.addView(text("Mis mensajes", 28, true))
        panel.addView(atenuado("Aquí verás las respuestas del administrador."))

        cargar()
    }

    private fun cargar() {
        RetrofitClient.api.misMensajes()
            .enqueue(object : Callback<ApiResponse<List<MensajeAlumno>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<MensajeAlumno>>>,
                    response: Response<ApiResponse<List<MensajeAlumno>>>
                ) {
                    if (isFinishing || isDestroyed) return
                    if (response.isSuccessful && response.body()?.success == true) {
                        mostrar(response.body()?.data.orEmpty())
                    } else {
                        Toast.makeText(
                            this@MisMensajesActivity,
                            "No se pudieron cargar tus mensajes.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<MensajeAlumno>>>, t: Throwable) {
                    if (isFinishing || isDestroyed) return
                    Toast.makeText(
                        this@MisMensajesActivity,
                        "Sin conexión. Revisa tu internet.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun mostrar(lista: List<MensajeAlumno>) {
        if (lista.isEmpty()) {
            panel.addView(atenuado("Todavía no has enviado mensajes."))
            return
        }

        lista.forEach { m ->
            val respondido = !m.respuesta.isNullOrBlank()
            val nueva = respondido && m.respuesta_vista == 0

            caja { inside ->
                when {
                    nueva -> inside.addView(insignia("Respuesta nueva"))
                    respondido -> inside.addView(insignia("Respondido"))
                    else -> inside.addView(atenuado("Esperando respuesta", 13))
                }
                inside.addView(text(m.pregunta, 16, true))
                inside.addView(atenuado((m.fecha_envio ?: "").take(16), 13))
                if (respondido) {
                    inside.addView(atenuado("Respuesta del administrador", 13))
                    inside.addView(text(m.respuesta.orEmpty(), 16))
                }
            }

            if (nueva) marcarVista(m.id_mensaje)
        }
    }

    // Se hace en silencio: si falla, el aviso simplemente seguirá apareciendo.
    private fun marcarVista(id: Int) {
        RetrofitClient.api.marcarRespuestaVista(MensajeVistoRequest(id))
            .enqueue(object : Callback<ApiResponse<JsonElement>> {
                override fun onResponse(
                    call: Call<ApiResponse<JsonElement>>,
                    response: Response<ApiResponse<JsonElement>>
                ) {}

                override fun onFailure(call: Call<ApiResponse<JsonElement>>, t: Throwable) {}
            })
    }
}
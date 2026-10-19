package com.example.four_ways_learn.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.four_ways_learn.R
import com.example.four_ways_learn.model.EnviarMensajeRequest
import com.example.four_ways_learn.model.MensajeCreado
import com.example.four_ways_learn.network.ApiResponse
import com.example.four_ways_learn.network.RetrofitClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DialogoMensajeAdmin {
    fun mostrar(context: Context) {
        val vista = LayoutInflater.from(context).inflate(R.layout.dialog_mensaje_admin, null)
        val contenedor = vista.findViewById<TextInputLayout>(R.id.layout_mensaje)
        val campo = vista.findViewById<TextInputEditText>(R.id.campo_mensaje)

        val dialogo = MaterialAlertDialogBuilder(context)
            .setTitle("Comunicarse con el administrador")
            .setView(vista)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Enviar", null)
            .create()

        dialogo.show()

        val botonEnviar = dialogo.getButton(AlertDialog.BUTTON_POSITIVE)

        fun reactivar(mensajeError: String) {
            if (!dialogo.isShowing) return
            contenedor.error = mensajeError
            botonEnviar.isEnabled = true
            campo.isEnabled = true
        }

        botonEnviar.setOnClickListener {
            val texto = campo.text?.toString()?.trim().orEmpty()
            if (texto.isEmpty()) {
                contenedor.error = "Escribe tu mensaje."
                return@setOnClickListener
            }

            contenedor.error = null
            botonEnviar.isEnabled = false
            campo.isEnabled = false

            RetrofitClient.api.enviarMensaje(EnviarMensajeRequest(texto))
                .enqueue(object : Callback<ApiResponse<MensajeCreado>> {
                    override fun onResponse(
                        call: Call<ApiResponse<MensajeCreado>>,
                        response: Response<ApiResponse<MensajeCreado>>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(
                                context,
                                "Mensaje enviado al administrador.",
                                Toast.LENGTH_LONG
                            ).show()
                            if (dialogo.isShowing) dialogo.dismiss()
                        } else {
                            reactivar(leerError(response))
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<MensajeCreado>>, t: Throwable) {
                        reactivar("Sin conexión. Revisa tu internet e inténtalo de nuevo.")
                    }
                })
        }
    }

    private fun leerError(response: Response<ApiResponse<MensajeCreado>>): String {
        val porDefecto = "No se pudo enviar el mensaje."
        return try {
            val cuerpo = response.errorBody()?.string() ?: return response.body()?.mensaje ?: porDefecto
            JsonParser.parseString(cuerpo).asJsonObject.get("mensaje")?.asString ?: porDefecto
        } catch (e: Exception) {
            porDefecto
        }
    }
}
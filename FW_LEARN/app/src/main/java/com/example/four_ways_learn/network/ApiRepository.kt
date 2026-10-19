package com.example.four_ways_learn.network

import android.content.Context
import androidx.annotation.StringRes
import com.example.four_ways_learn.R
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

data class ApiError(
    val mensaje: String? = null,
    val mensajeEstado: Int = 0,
    @param:StringRes val mensajeRes: Int = R.string.error_generico,
    val esErrorConexion: Boolean = false
) {
    fun localizedMessage(context: Context): String {
        if (!mensaje.isNullOrBlank()) {
            return mensaje
        }

        if (mensajeRes == R.string.error_servidor) {
            return context.getString(R.string.error_servidor, mensajeEstado)
        }

        return context.getString(mensajeRes)
    }
}

object ApiRepository {

    fun <T> execute(
        call: Call<ApiResponse<T>>,
        success: (T?) -> Unit,
        alFallo: (ApiError) -> Unit
    ) {
        call.enqueue(object : Callback<ApiResponse<T>> {

            override fun onResponse(
                call: Call<ApiResponse<T>>,
                response: Response<ApiResponse<T>>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null && body.success) {
                    success(body.data)
                } else {
                    var mensaje = body?.mensaje

                    try {
                        val contenidoError = response.errorBody()?.string()

                        if (!contenidoError.isNullOrBlank()) {
                            val json = JSONObject(contenidoError)
                            val mensajeServidor = json.optString("mensaje")

                            if (mensajeServidor.isNotBlank()) {
                                mensaje = mensajeServidor
                            }
                        }
                    } catch (error: Exception) {
                        mensaje = body?.mensaje
                    }

                    val error = ApiError(
                        mensaje = mensaje,
                        mensajeEstado = response.code(),
                        mensajeRes = R.string.error_servidor
                    )

                    alFallo(error)
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<T>>,
                t: Throwable
            ) {
                if (call.isCanceled) {
                    return
                }

                val esErrorConexion = t is IOException

                val mensajeRes: Int

                if (esErrorConexion) {
                    mensajeRes = R.string.error_conexion
                } else {
                    mensajeRes = R.string.error_generico
                }

                val error = ApiError(
                    mensajeRes = mensajeRes,
                    esErrorConexion = esErrorConexion
                )

                alFallo(error)
            }
        })
    }
}

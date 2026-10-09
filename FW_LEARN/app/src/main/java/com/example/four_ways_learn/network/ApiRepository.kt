package com.example.four_ways_learn.network

import android.content.Context
import androidx.annotation.StringRes
import com.example.four_ways_learn.R
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ApiError(
    val mensaje: String? = null,
    val status: Int = 0,
    @param:StringRes val mensajeRes: Int = R.string.error_generic
) {
    fun localizedMessage(context: Context): String =
        mensaje?.takeIf { it.isNotBlank() }
            ?: when (mensajeRes) {
                R.string.error_server -> context.getString(R.string.error_server, status)
                else -> context.getString(mensajeRes)
            }
}

object ApiRepository {
    fun <T> execute(
        call: Call<ApiResponse<T>>,
        success: (T?) -> Unit,
        failure: (ApiError) -> Unit
    ) {
        call.enqueue(object : Callback<ApiResponse<T>> {
            override fun onResponse(
                call: Call<ApiResponse<T>>,
                response: Response<ApiResponse<T>>
            ) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true) success(body.data)
                else {
                    val message = runCatching {
                        JSONObject(
                            response.errorBody()?.string().orEmpty()
                        ).optString("mensaje")
                    }.getOrNull()
                    failure(
                        ApiError(
                            mensaje = message?.takeIf { it.isNotBlank() }
                                ?: body?.mensaje?.takeIf { it.isNotBlank() },
                            status = response.code(),
                            mensajeRes = R.string.error_server
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
                if (!call.isCanceled) failure(ApiError(mensajeRes = R.string.error_connection))
            }
        })
    }
}

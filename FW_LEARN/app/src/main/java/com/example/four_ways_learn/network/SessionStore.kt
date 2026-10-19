package com.example.four_ways_learn.network

import android.content.Context
import com.google.gson.Gson
import android.content.SharedPreferences

object SessionStore {
    private lateinit var prefs: SharedPreferences
    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(
            "fw_session_v2",
            Context.MODE_PRIVATE
        )
    }

    val token: String? get() = prefs.getString("token", null)
    val usuario: Usuario?
        get() = runCatching {
            Gson().fromJson(
                prefs.getString("usuario", null),
                Usuario::class.java
            )
        }.getOrNull()

    fun guardar(data: LoginData) {
        prefs.edit().putString("token", data.token)
            .putString("usuario", Gson().toJson(data.usuario)).apply()
    }

    fun actualizar(usuario: Usuario) {
        prefs.edit().putString("usuario", Gson().toJson(usuario)).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}

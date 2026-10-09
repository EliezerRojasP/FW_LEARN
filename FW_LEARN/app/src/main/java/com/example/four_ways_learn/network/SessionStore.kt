package com.example.four_ways_learn.network

import android.content.Context
import com.google.gson.Gson

object SessionStore {
    private lateinit var app: Context
    fun init(context: Context) {
        app = context.applicationContext
    }

    private val prefs get() = app.getSharedPreferences("fw_session_v2", Context.MODE_PRIVATE)
    val token: String? get() = prefs.getString("token", null)
    val usuario: Usuario?
        get() = runCatching {
            Gson().fromJson(
                prefs.getString("usuario", null),
                Usuario::class.java
            )
        }.getOrNull()

    fun save(data: LoginData) {
        prefs.edit().putString("token", data.token)
            .putString("usuario", Gson().toJson(data.usuario)).apply()
    }

    fun update(user: Usuario) {
        prefs.edit().putString("usuario", Gson().toJson(user)).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}

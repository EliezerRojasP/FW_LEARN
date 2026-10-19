package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class UsuarioAdmin(
    @SerializedName("id_usuario") val id: Int? = null,
    @SerializedName("id_rol") val idRol: Int = ROL_ALUMNO,
    val nombre: String = "",
    val apellido: String = "",
    val usuario: String = "",
    val password: String? = null,
    val estado: Int = 1
) {
    companion object {
        const val ROL_ADMIN = 1
        const val ROL_ALUMNO = 2
    }
}


data class IdUsuario(
    @SerializedName("id_usuario") val id: Int
)

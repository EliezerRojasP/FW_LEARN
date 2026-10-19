package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class LeccionAdmin(
    @SerializedName("id_leccion") val id: Int? = null,
    @SerializedName("id_topico") val idTopico: Int,
    val titulo: String = "",
    val contenido: String = "",
    val orden: Int = 1,
    val estado: Int = 1
)

data class IdLeccion(
    @SerializedName("id_leccion") val id: Int
)

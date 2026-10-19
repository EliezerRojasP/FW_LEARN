package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class TopicoAdmin(
    @SerializedName("id_topico") val id: Int? = null,
    @SerializedName("id_curso") val idCurso: Int,
    val nombre: String = "",
    val descripcion: String? = null,
    val orden: Int = 1,
    val estado: Int = 1
)

data class IdTopico(
    @SerializedName("id_topico") val id: Int
)

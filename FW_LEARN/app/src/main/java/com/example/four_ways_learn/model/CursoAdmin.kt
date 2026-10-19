package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class CursoAdmin(
    @SerializedName("id_curso") val id: Int? = null,
    val nombre: String = "",
    val descripcion: String? = null,
    val estado: Int = 1
)


data class IdCurso(
    @SerializedName("id_curso") val id: Int
)

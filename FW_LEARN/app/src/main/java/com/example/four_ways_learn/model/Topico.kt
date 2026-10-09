package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class Topico(
    @SerializedName("id_topico") val id: Int,
    @SerializedName("id_curso") val idCurso: Int,
    val nombre: String,
    val descripcion: String?,
    val orden: Int,
    val estado: Int,
    val total_lecciones: Int = 0,
    val lecciones_completadas: Int = 0,
    val porcentaje_avance: Double = 0.0
)

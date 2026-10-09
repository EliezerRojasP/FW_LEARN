package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class Pregunta(
    @SerializedName("id_pregunta")
    val id: Int,

    @SerializedName("id_leccion")
    val idLeccion: Int,

    @SerializedName("enunciado")
    val enunciado: String,

    @SerializedName("orden")
    val orden: Int,

    @SerializedName("estado")
    val estado: Int
)
package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class Alternativa(
    @SerializedName("id_alternativa") val id: Int,
    @SerializedName("id_pregunta") val idPregunta: Int,
    val texto: String
)

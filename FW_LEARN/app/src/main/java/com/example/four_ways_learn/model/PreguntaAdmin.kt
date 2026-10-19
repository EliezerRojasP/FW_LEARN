package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class AlternativaAdmin(
    @SerializedName("id_alternativa") val id: Int? = null,
    val texto: String = "",
    @SerializedName("es_correcta") val esCorrecta: Int = 0
)

data class PreguntaAdmin(
    @SerializedName("id_pregunta") val id: Int? = null,
    @SerializedName("id_leccion") val idLeccion: Int,
    val enunciado: String = "",
    val orden: Int = 1,
    val estado: Int = 1,
    val alternativas: List<AlternativaAdmin> = emptyList()
)


data class IdPregunta(
    @SerializedName("id_pregunta") val id: Int
)

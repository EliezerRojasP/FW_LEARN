package com.example.four_ways_learn.model

import com.google.gson.annotations.SerializedName

data class EnviarMensajeRequest(
    val pregunta: String
)
data class MensajeCreado(
    @SerializedName("id_mensaje") val id: Int
)
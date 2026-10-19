package com.example.four_ways_learn.model

data class MensajeAlumno(
    val id_mensaje: Int,
    val pregunta: String,
    val respuesta: String?,
    val fecha_envio: String?,
    val fecha_respuesta: String?,
    val respuesta_vista: Int = 0
)

data class MensajeVistoRequest(
    val id_mensaje: Int
)
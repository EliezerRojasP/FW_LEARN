package com.example.four_ways_learn.model

data class ResultadoAdmin(
    val titulo: String = "",
    val alumno: String = "",
    val puntaje: Double = 0.0,
    val total_preguntas: Int = 0,
    val porcentaje: Double = 0.0,
    val fecha_resultado: String = ""
)

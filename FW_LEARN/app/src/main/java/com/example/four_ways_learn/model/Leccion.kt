package com.example.four_ways_learn.model

data class Leccion(
    val id_leccion: Int,
    val id_topico: Int,
    val titulo: String,
    val contenido: String?,
    val orden: Int,
    val estado: Int,
    val total_preguntas: Int = 0,
    val completada: Int = 0,
    val mejor_puntaje: Double = 0.0,
    val porcentaje_avance: Double = 0.0
)

package com.example.four_ways_learn.model

data class Curso(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val total_lecciones: Int = 0,
    val lecciones_completadas: Int = 0,
    val porcentaje_avance: Double = 0.0
)

package com.example.four_ways_learn.local

object TopicoTabla {

    const val TABLA_TOPICOS = "topicos"
    const val COLUMNA_ID_USUARIO = "id_usuario"
    const val COLUMNA_ID_CURSO = "id_curso"
    const val COLUMNA_ID_TOPICO = "id_topico"
    const val COLUMNA_NOMBRE = "nombre"
    const val COLUMNA_DESCRIPCION = "descripcion"
    const val COLUMNA_ORDEN = "orden"
    const val COLUMNA_ESTADO = "estado"
    const val COLUMNA_TOTAL_LECCIONES = "total_lecciones"
    const val COLUMNA_LECCIONES_COMPLETADAS = "lecciones_completadas"
    const val COLUMNA_PORCENTAJE_AVANCE = "porcentaje_avance"

    const val SQL_CREAR_TABLA = """
        CREATE TABLE $TABLA_TOPICOS (
            $COLUMNA_ID_USUARIO INTEGER NOT NULL,
            $COLUMNA_ID_CURSO INTEGER NOT NULL,
            $COLUMNA_ID_TOPICO INTEGER NOT NULL,
            $COLUMNA_NOMBRE TEXT NOT NULL,
            $COLUMNA_DESCRIPCION TEXT,
            $COLUMNA_ORDEN INTEGER NOT NULL,
            $COLUMNA_ESTADO INTEGER NOT NULL,
            $COLUMNA_TOTAL_LECCIONES INTEGER NOT NULL DEFAULT 0,
            $COLUMNA_LECCIONES_COMPLETADAS INTEGER NOT NULL DEFAULT 0,
            $COLUMNA_PORCENTAJE_AVANCE REAL NOT NULL DEFAULT 0,
            PRIMARY KEY (
                $COLUMNA_ID_USUARIO,
                $COLUMNA_ID_CURSO,
                $COLUMNA_ID_TOPICO
            )
        )
    """
}


package com.example.four_ways_learn.local

object CursoTabla {
    const val NOMBRE_BASE_DATOS = "fw_learn_local.db"
    const val VERSION_BASE_DATOS = 2
    const val TABLA_CURSOS = "cursos"
    const val COLUMNA_ID_USUARIO = "id_usuario"
    const val COLUMNA_ID_CURSO = "id_curso"
    const val COLUMNA_NOMBRE = "nombre"
    const val COLUMNA_DESCRIPCION = "descripcion"
    const val COLUMNA_TOTAL_LECCIONES = "total_lecciones"
    const val COLUMNA_LECCIONES_COMPLETADAS = "lecciones_completadas"
    const val COLUMNA_PORCENTAJE_AVANCE = "porcentaje_avance"

    const val SQL_CREAR_TABLA = """
        CREATE TABLE $TABLA_CURSOS (
            $COLUMNA_ID_USUARIO INTEGER NOT NULL,
            $COLUMNA_ID_CURSO INTEGER NOT NULL,
            $COLUMNA_NOMBRE TEXT NOT NULL,
            $COLUMNA_DESCRIPCION TEXT NOT NULL,
            $COLUMNA_TOTAL_LECCIONES INTEGER NOT NULL DEFAULT 0,
            $COLUMNA_LECCIONES_COMPLETADAS INTEGER NOT NULL DEFAULT 0,
            $COLUMNA_PORCENTAJE_AVANCE REAL NOT NULL DEFAULT 0,
            PRIMARY KEY ($COLUMNA_ID_USUARIO, $COLUMNA_ID_CURSO)
        )
    """

}
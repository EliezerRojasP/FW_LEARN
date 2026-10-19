package com.example.four_ways_learn.local

import android.content.ContentValues
import android.content.Context
import com.example.four_ways_learn.model.Curso
import java.io.Closeable

class CursoStore(context: Context) : Closeable {

    private val dbHelper =
        LocalDatabaseHelper(context.applicationContext)

    fun guardarCursos(idUsuario: Int, cursos: List<Curso>){
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        try {
            db.delete(
                CursoTabla.TABLA_CURSOS,
                "${CursoTabla.COLUMNA_ID_USUARIO} = ?",
                arrayOf(idUsuario.toString())
            )

            cursos.forEach { curso ->
                val valores = ContentValues().apply {
                    put(CursoTabla.COLUMNA_ID_USUARIO, idUsuario)
                    put(CursoTabla.COLUMNA_ID_CURSO, curso.id)
                    put(CursoTabla.COLUMNA_NOMBRE, curso.nombre)
                    put(CursoTabla.COLUMNA_DESCRIPCION, curso.descripcion)
                    put(
                        CursoTabla.COLUMNA_TOTAL_LECCIONES,
                        curso.total_lecciones
                    )
                    put(
                        CursoTabla.COLUMNA_LECCIONES_COMPLETADAS,
                        curso.lecciones_completadas
                    )
                    put(
                        CursoTabla.COLUMNA_PORCENTAJE_AVANCE,
                        curso.porcentaje_avance
                    )
                }

                db.replaceOrThrow(
                    CursoTabla.TABLA_CURSOS,
                    null,
                    valores
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerCursos(idUsuario: Int): List<Curso>{
        val cursos = mutableListOf<Curso>()

        dbHelper.readableDatabase.query(
            CursoTabla.TABLA_CURSOS,
            null,
            "${CursoTabla.COLUMNA_ID_USUARIO} = ?",
            arrayOf(idUsuario.toString()),
            null,
            null,
            "${CursoTabla.COLUMNA_ID_CURSO} ASC"
        ).use { cursor ->
            val indiceId =
                cursor.getColumnIndexOrThrow(CursoTabla.COLUMNA_ID_CURSO)
            val indiceNombre =
                cursor.getColumnIndexOrThrow(CursoTabla.COLUMNA_NOMBRE)
            val indiceDescripcion =
                cursor.getColumnIndexOrThrow(CursoTabla.COLUMNA_DESCRIPCION)
            val indiceTotal =
                cursor.getColumnIndexOrThrow(CursoTabla.COLUMNA_TOTAL_LECCIONES)
            val indiceCompletadas =
                cursor.getColumnIndexOrThrow(CursoTabla.COLUMNA_LECCIONES_COMPLETADAS)
            val indicePorcentaje =
                cursor.getColumnIndexOrThrow(CursoTabla.COLUMNA_PORCENTAJE_AVANCE)

            while (cursor.moveToNext()){
                cursos.add(
                    Curso(
                        id = cursor.getInt(indiceId),
                        nombre = cursor.getString(indiceNombre),
                        descripcion = cursor.getString(indiceDescripcion),
                        total_lecciones = cursor.getInt(indiceTotal),
                        lecciones_completadas = cursor.getInt(indiceCompletadas),
                        porcentaje_avance = cursor.getDouble(indicePorcentaje)
                    )
                )
            }
        }

        return cursos
    }
    override fun close(){
        dbHelper.close()
    }

}
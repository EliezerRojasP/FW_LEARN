package com.example.four_ways_learn.local

import android.content.ContentValues
import android.content.Context
import com.example.four_ways_learn.model.Topico
import java.io.Closeable

class TopicoStore(context: Context) : Closeable {

    private val dbHelper =
        LocalDatabaseHelper(context.applicationContext)

    fun guardarTopicos(
        idUsuario: Int,
        idCurso: Int,
        topicos: List<Topico>
    ) {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        try {
            db.delete(
                TopicoTabla.TABLA_TOPICOS,
                "${TopicoTabla.COLUMNA_ID_USUARIO} = ? AND " +
                        "${TopicoTabla.COLUMNA_ID_CURSO} = ?",
                arrayOf(
                    idUsuario.toString(),
                    idCurso.toString()
                )
            )

            for (topico in topicos) {
                val valores = ContentValues().apply {
                    put(TopicoTabla.COLUMNA_ID_USUARIO, idUsuario)
                    put(TopicoTabla.COLUMNA_ID_CURSO, idCurso)
                    put(TopicoTabla.COLUMNA_ID_TOPICO, topico.id)
                    put(TopicoTabla.COLUMNA_NOMBRE, topico.nombre)
                    put(TopicoTabla.COLUMNA_DESCRIPCION, topico.descripcion)
                    put(TopicoTabla.COLUMNA_ORDEN, topico.orden)
                    put(TopicoTabla.COLUMNA_ESTADO, topico.estado)

                    put(
                        TopicoTabla.COLUMNA_TOTAL_LECCIONES,
                        topico.total_lecciones
                    )

                    put(
                        TopicoTabla.COLUMNA_LECCIONES_COMPLETADAS,
                        topico.lecciones_completadas
                    )

                    put(
                        TopicoTabla.COLUMNA_PORCENTAJE_AVANCE,
                        topico.porcentaje_avance
                    )
                }

                db.replaceOrThrow(
                    TopicoTabla.TABLA_TOPICOS,
                    null,
                    valores
                )
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerTopicos(
        idUsuario: Int,
        idCurso: Int
    ): List<Topico> {
        val topicos = mutableListOf<Topico>()

        dbHelper.readableDatabase.query(
            TopicoTabla.TABLA_TOPICOS,
            null,
            "${TopicoTabla.COLUMNA_ID_USUARIO} = ? AND " +
                    "${TopicoTabla.COLUMNA_ID_CURSO} = ?",
            arrayOf(
                idUsuario.toString(),
                idCurso.toString()
            ),
            null,
            null,
            "${TopicoTabla.COLUMNA_ORDEN} ASC, " +
                    "${TopicoTabla.COLUMNA_ID_TOPICO} ASC"
        ).use { cursor ->
            val indiceId =
                cursor.getColumnIndexOrThrow(TopicoTabla.COLUMNA_ID_TOPICO)
            val indiceNombre =
                cursor.getColumnIndexOrThrow(TopicoTabla.COLUMNA_NOMBRE)
            val indiceDescripcion =
                cursor.getColumnIndexOrThrow(TopicoTabla.COLUMNA_DESCRIPCION)
            val indiceOrden =
                cursor.getColumnIndexOrThrow(TopicoTabla.COLUMNA_ORDEN)
            val indiceEstado =
                cursor.getColumnIndexOrThrow(TopicoTabla.COLUMNA_ESTADO)
            val indiceTotal =
                cursor.getColumnIndexOrThrow(TopicoTabla.COLUMNA_TOTAL_LECCIONES)
            val indiceCompletadas =
                cursor.getColumnIndexOrThrow(
                    TopicoTabla.COLUMNA_LECCIONES_COMPLETADAS
                )
            val indicePorcentaje =
                cursor.getColumnIndexOrThrow(
                    TopicoTabla.COLUMNA_PORCENTAJE_AVANCE
                )

            while (cursor.moveToNext()) {
                val descripcion = if (cursor.isNull(indiceDescripcion)) {
                    null
                } else {
                    cursor.getString(indiceDescripcion)
                }

                topicos.add(
                    Topico(
                        id = cursor.getInt(indiceId),
                        idCurso = idCurso,
                        nombre = cursor.getString(indiceNombre),
                        descripcion = descripcion,
                        orden = cursor.getInt(indiceOrden),
                        estado = cursor.getInt(indiceEstado),
                        total_lecciones = cursor.getInt(indiceTotal),
                        lecciones_completadas =
                            cursor.getInt(indiceCompletadas),
                        porcentaje_avance =
                            cursor.getDouble(indicePorcentaje)
                    )
                )
            }
        }

        return topicos
    }

    override fun close() {
        dbHelper.close()
    }
}
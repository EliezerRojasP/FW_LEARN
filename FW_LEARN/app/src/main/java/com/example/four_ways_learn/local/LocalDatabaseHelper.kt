package com.example.four_ways_learn.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper (
    context.applicationContext,
    CursoTabla.NOMBRE_BASE_DATOS,
    null,
    CursoTabla.VERSION_BASE_DATOS
) {
    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(CursoTabla.SQL_CREAR_TABLA)
        db.execSQL(TopicoTabla.SQL_CREAR_TABLA)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ){
        if (oldVersion < 2){
            db.execSQL(TopicoTabla.SQL_CREAR_TABLA)
        }
    }

}
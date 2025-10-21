package com.example.miapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConexionDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS USUARIOS (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                NOMBRE TEXT NOT NULL,
                APELLIDOS TEXT NOT NULL,
                EMAIL TEXT UNIQUE NOT NULL,
                CLAVE TEXT NOT NULL,
                CLAVESEC TEXT
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS CODIGOS_RECUPERACION (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                EMAIL TEXT NOT NULL,
                CODIGO INTEGER NOT NULL,
                EXPIRA INTEGER NOT NULL
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO USUARIOS (NOMBRE, APELLIDOS, EMAIL, CLAVE, CLAVESEC)
            VALUES ('Marlon', 'Tabilo Araya', 'admin@admin.cl', 'Admin123', 'marlon1');
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS USUARIOS;")
        db.execSQL("DROP TABLE IF EXISTS CODIGOS_RECUPERACION;")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "CRUD.db"
        private const val DATABASE_VERSION = 8
    }
}

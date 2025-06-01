package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "club.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"
        const val COLUMN_DNI = "dni"
        const val COLUMN_DIRECCION = "direccion"
        const val COLUMN_TIPO = "tipo" // "Socio" o "No Socio"
        const val COLUMN_APTO = "aptoMedico"

        const val TABLE_ADMINS = "admins"
        const val COLUMN_USERNAME = "usuario"
        const val COLUMN_PASSWORD = "password"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
        CREATE TABLE $TABLE_USERS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NOMBRE TEXT,
            $COLUMN_APELLIDO TEXT,
            $COLUMN_DNI TEXT UNIQUE,
            $COLUMN_DIRECCION TEXT,
            $COLUMN_TIPO TEXT,
            $COLUMN_APTO INTEGER
        )
    """.trimIndent()

        val createAdminsTable = """
        CREATE TABLE $TABLE_ADMINS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USERNAME TEXT UNIQUE,
            $COLUMN_PASSWORD TEXT
        )
    """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createAdminsTable)

        // 👇 Seed básico con execSQL directamente
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('admin1', '1234')")
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('lucas', 'admin')")
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('sato', 'clave123')")
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun verificarLoginAdmin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMINS,
            null,
            "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?",
            arrayOf(username, password),
            null, null, null
        )
        val success = cursor.count > 0
        cursor.close()
        return success
    }

    fun insertarUsuario(nombre: String, apellido: String, dni: String, direccion: String, tipo: String, apto: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_DNI, dni)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_TIPO, tipo)
            put(COLUMN_APTO, if (apto) 1 else 0)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun actualizarUsuario(dni: String, nombre: String, apellido: String, direccion: String, tipo: String, apto: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_TIPO, tipo)
            put(COLUMN_APTO, if (apto) 1 else 0)
        }
        return db.update(TABLE_USERS, values, "$COLUMN_DNI=?", arrayOf(dni))
    }

    fun obtenerUsuarioPorDNI(dni: String): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_DNI=?",
            arrayOf(dni),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val usuario = mapOf(
                COLUMN_NOMBRE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                COLUMN_APELLIDO to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO)),
                COLUMN_DIRECCION to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION)),
                COLUMN_TIPO to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                COLUMN_APTO to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APTO)).toString()
            )
            cursor.close()
            usuario
        } else {
            cursor.close()
            null
        }
    }
}

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

        const val TABLE_ACTIVITIES = "activities"
        const val COLUMN_PRECIO = "precio"

        const val TABLE_CUOTAS = "cuotas"

        const val TABLE_ACTIVITIES_PAYMENTS = "actividad_pagos"
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

        val createActivitiesTable = """
        CREATE TABLE $TABLE_ACTIVITIES (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NOMBRE TEXT,
            $COLUMN_PRECIO REAL
        )
    """.trimIndent()

        val createCuotasTable = """
        CREATE TABLE $TABLE_CUOTAS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            fecha_vencimiento TEXT,
            pagado INTEGER,
            FOREIGN KEY(id_usuario) REFERENCES users(id)
        )
    """.trimIndent()

        val createPagosActividadTable = """
        CREATE TABLE $TABLE_ACTIVITIES_PAYMENTS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            id_actividad INTEGER,
            fecha TEXT,
            FOREIGN KEY(id_usuario) REFERENCES users(id),
            FOREIGN KEY(id_actividad) REFERENCES activities(id)
        )
    """.trimIndent()

        db?.execSQL(createActivitiesTable)
        db?.execSQL(createCuotasTable)
        db?.execSQL(createPagosActividadTable)

        db?.execSQL(createUsersTable)
        db?.execSQL(createAdminsTable)

        // 👇 Seed básico con execSQL directamente
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('admin1', '1234')")
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('lucas', 'admin')")
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('sato', 'clave123')")
        db?.execSQL("INSERT INTO $TABLE_ACTIVITIES (nombre, precio) VALUES ('Fútbol', 1500.0)")
        db?.execSQL("INSERT INTO $TABLE_ACTIVITIES (nombre, precio) VALUES ('Natación', 2000.0)")
        db?.execSQL("INSERT INTO $TABLE_ACTIVITIES (nombre, precio) VALUES ('Yoga', 1800.0)")

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADMINS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES_PAYMENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CUOTAS")
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

    // Registrar nueva actividad
    fun insertarActividad(nombre: String, precio: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("precio", precio)
        }
        return db.insert("activities", null, values)
    }

    // Agregar cuota para socio
    fun insertarCuota(idUsuario: Int, fechaVencimiento: String, pagado: Boolean = false): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("fecha_vencimiento", fechaVencimiento)
            put("pagado", if (pagado) 1 else 0)
        }
        return db.insert("cuotas", null, values)
    }

    // Listar cuotas que vencen HOY
    fun cuotasVencenHoy(): List<Map<String, String>> {
        val db = readableDatabase
        val hoy = java.time.LocalDate.now().toString()
        val cursor = db.rawQuery("SELECT u.nombre, u.apellido, c.fecha_vencimiento FROM cuotas c JOIN users u ON c.id_usuario = u.id WHERE c.fecha_vencimiento = ? AND c.pagado = 0", arrayOf(hoy))

        val lista = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            lista.add(
                mapOf(
                    "nombre" to cursor.getString(0),
                    "apellido" to cursor.getString(1),
                    "fecha" to cursor.getString(2)
                )
            )
        }
        cursor.close()
        return lista
    }

    fun obtenerVencimientosProximos(diasAdelante: Int = 0): List<String> {
        val db = readableDatabase
        val resultados = mutableListOf<String>()

        val query = """
        SELECT u.nombre, u.apellido, c.fecha_vencimiento
        FROM cuotas c
        INNER JOIN users u ON c.id_usuario = u.id
        WHERE c.pagado = 0 AND date(c.fecha_vencimiento) <= date('now', '+$diasAdelante days')
        ORDER BY c.fecha_vencimiento ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val fecha = cursor.getString(2)
            resultados.add("$nombre $apellido - Cuota vence: $fecha")
        }

        cursor.close()
        return resultados
    }

    fun pagarCuota(idCuota: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("pagado", 1)
        }
        return db.update("cuotas", values, "id = ?", arrayOf(idCuota.toString()))
    }

    fun registrarPagoActividad(idUsuario: Int, idActividad: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("id_actividad", idActividad)
            put("fecha", java.time.LocalDate.now().toString())
        }
        return db.insert("actividad_pagos", null, values)
    }

    fun verCuotasDeSocio(dni: String): List<Map<String, String>> {
        val db = readableDatabase
        val idCursor = db.rawQuery("SELECT id FROM users WHERE dni = ?", arrayOf(dni))

        if (!idCursor.moveToFirst()) {
            idCursor.close()
            return emptyList()
        }

        val idUsuario = idCursor.getInt(0)
        idCursor.close()

        val cuotasCursor = db.rawQuery(
            "SELECT fecha_vencimiento, pagado FROM cuotas WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        val lista = mutableListOf<Map<String, String>>()
        while (cuotasCursor.moveToNext()) {
            lista.add(
                mapOf(
                    "fecha_vencimiento" to cuotasCursor.getString(0),
                    "pagado" to if (cuotasCursor.getInt(1) == 1) "Sí" else "No"
                )
            )
        }

        cuotasCursor.close()
        return lista
    }

    fun obtenerTodasLasActividades(): List<String> {
        val db = readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT nombre, precio FROM actividades", null)

        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val precio = cursor.getDouble(1)
            lista.add("$nombre - \$${precio}")
        }

        cursor.close()
        return lista
    }

}

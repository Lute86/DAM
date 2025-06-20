package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Build
import androidx.annotation.RequiresApi

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "club.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"
        const val COLUMN_DNI = "dni"
        const val COLUMN_CELULAR = "celular"
        const val COLUMN_EMAIL = "email"
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
            $COLUMN_CELULAR TEXT,
            $COLUMN_EMAIL TEXT,
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
        CREATE TABLE actividades (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            precio REAL,
            cupo INTEGER
        )
    """.trimIndent()

        val createCuotasTable = """
        CREATE TABLE cuotas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            fecha_vencimiento TEXT,
            pagado INTEGER,
            FOREIGN KEY(id_usuario) REFERENCES users(id)
        )
    """.trimIndent()

        val createPagosActividadTable = """
        CREATE TABLE actividad_pagos (
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
        db?.execSQL("INSERT INTO actividades (nombre, precio, cupo) VALUES ('Fútbol', 1500.0, 20)")
        db?.execSQL("INSERT INTO actividades (nombre, precio, cupo) VALUES ('Natación', 2000.0, 20)")
        db?.execSQL("INSERT INTO actividades (nombre, precio, cupo) VALUES ('Yoga', 1800.0, 20)")
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
        db.close()
        return success
    }

    fun insertarUsuario(nombre: String, apellido: String, dni: String, celular: String, email: String, direccion: String, tipo: String, apto: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_DNI, dni)
            put(COLUMN_CELULAR, celular)
            put(COLUMN_EMAIL, email)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_TIPO, tipo)
            put(COLUMN_APTO, if (apto) 1 else 0)
        }
        return db.insert(TABLE_USERS, null, values)

    }

    fun actualizarUsuario(dni: String, nombre: String, apellido: String, celular: String, email: String, direccion: String, tipo: String, apto: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_CELULAR, celular)
            put(COLUMN_EMAIL, email)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_TIPO, tipo)
            put(COLUMN_APTO, if (apto) 1 else 0)
        }
        return db.update(TABLE_USERS, values, "$COLUMN_DNI=?", arrayOf(dni))
    }

    fun obtenerUsuarioPorDNI(dni: String): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "dni = ?",
            arrayOf(dni),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val usuario = mapOf(
                "nombre" to cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                "apellido" to cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                "celular" to cursor.getString(cursor.getColumnIndexOrThrow("celular")),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow("email")),
                "direccion" to cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                "tipo" to cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                "aptoMedico" to cursor.getString(cursor.getColumnIndexOrThrow("aptoMedico"))
            )
            cursor.close()
            return usuario
        }
        cursor.close()
        return null
    }

    fun eliminarUsuarioPorDNI(dni: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USERS, "dni = ?", arrayOf(dni))
    }


    fun obtenerUsuariosPorTipo(tipo: String): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_TIPO = ?", arrayOf(tipo))

        val lista = mutableListOf<Map<String, String>>()

        while (cursor.moveToNext()) {
            val usuario = mapOf(
                "nombre" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                "apellido" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO)),
                "dni" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DNI)),
                "celular" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CELULAR)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                "direccion" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION))
            )
            lista.add(usuario)
        }

        cursor.close()
        db.close()
        return lista
    }

    fun usuarioExiste(dni: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM users WHERE dni = ?", arrayOf(dni))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // Registrar nueva actividad
    fun insertarActividad(nombre: String, precio: Double, cupo: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("precio", precio)
            put("cupo", cupo)
        }
        return db.insert("actividades", null, values)
    }

    // Agregar cuota para socio
    fun insertarCuota(idUsuario: Int, fechaVencimiento: String, monto: Double, pagado: Boolean = false): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("fecha_vencimiento", fechaVencimiento)
            put("monto", monto)
            put("pagado", if (pagado) 1 else 0)
        }
        return db.insert("cuotas", null, values)
    }

    // Listar cuotas que vencen HOY
    @RequiresApi(Build.VERSION_CODES.O)
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
          //  put("fecha", java.time.LocalDate.now().toString())
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

    fun obtenerTodasLasActividades(): List<Map<String, String>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery("SELECT nombre, precio, cupo FROM actividades", null)

        while (cursor.moveToNext()) {
            val actividad = mapOf(
                "nombre" to cursor.getString(0),
                "precio" to cursor.getDouble(1).toString(),
                "cupo" to cursor.getInt(2).toString()
            )
            lista.add(actividad)
        }

        cursor.close()
        return lista
    }

    fun obtenerActividadPorNombre(nombre: String): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM actividades WHERE nombre = ?", arrayOf(nombre))

        return if (cursor.moveToFirst()) {
            val actividad = mapOf(
                "nombre" to cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                "precio" to cursor.getDouble(cursor.getColumnIndexOrThrow("precio")).toString(),
                "cupo" to cursor.getInt(cursor.getColumnIndexOrThrow("cupo")).toString()
            )
            cursor.close()
            actividad
        } else {
            cursor.close()
            null
        }
    }

    fun actualizarActividad(nombreOriginal: String, nuevoNombre: String, nuevoPrecio: Double, nuevoCupo: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nuevoNombre)
            put("precio", nuevoPrecio)
            put("cupo", nuevoCupo)
        }
        return db.update("actividades", values, "nombre = ?", arrayOf(nombreOriginal))
    }

    fun eliminarActividadPorNombre(nombre: String): Int {
        val db = writableDatabase
        return db.delete("actividades", "nombre = ?", arrayOf(nombre))
    }





}

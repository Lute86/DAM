package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.Calendar

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "club.db"
        const val DATABASE_VERSION = 2

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
            idSocio INTEGER,
            monto REAL,
            fechaPago TEXT,
            fechaVencimiento TEXT,
            estado TEXT,
            FOREIGN KEY (idSocio) REFERENCES users(id)
        );

    """.trimIndent()

        val createPagosActividadTable = """
        CREATE TABLE actividad_pagos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            id_actividad INTEGER,
            fecha TEXT,
            FOREIGN KEY(id_usuario) REFERENCES users(id),
            FOREIGN KEY(id_actividad) REFERENCES actividades(id)
        )
    """.trimIndent()

        db?.execSQL(createActivitiesTable)
        db?.execSQL(createCuotasTable)
        db?.execSQL(createPagosActividadTable)

        db?.execSQL(createUsersTable)
        db?.execSQL(createAdminsTable)

        // 👇 Seed básico con execSQL directamente
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('admin', '1234')")
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('lucas', 'admin')")
        db?.execSQL("INSERT INTO $TABLE_ADMINS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES ('sato', 'clave123')")
        db?.execSQL("INSERT INTO actividades (nombre, precio, cupo) VALUES ('Fútbol', 1500.0, 20)")
        db?.execSQL("INSERT INTO actividades (nombre, precio, cupo) VALUES ('Natación', 2000.0, 20)")
        db?.execSQL("INSERT INTO actividades (nombre, precio, cupo) VALUES ('Yoga', 1800.0, 20)")
        db?.execSQL("INSERT INTO $TABLE_USERS ($COLUMN_NOMBRE, $COLUMN_APELLIDO, $COLUMN_DNI, " +
                "$COLUMN_CELULAR, $COLUMN_EMAIL, $COLUMN_DIRECCION, $COLUMN_TIPO, $COLUMN_APTO) " +
                "VALUES ('Ana', 'Falsa', '123456789', '0000001', 'ana@falsa.com', 'Calle 123', 'Socio', 1)")
        db?.execSQL("INSERT INTO $TABLE_USERS ($COLUMN_NOMBRE, $COLUMN_APELLIDO, $COLUMN_DNI, " +
                "$COLUMN_CELULAR, $COLUMN_EMAIL, $COLUMN_DIRECCION, $COLUMN_TIPO, $COLUMN_APTO) " +
                "VALUES ('Juan', 'Falso', '123456777', '0000002', 'juan@falso.com', 'Calle 13', 'Socio', 1)")
        db?.execSQL("INSERT INTO $TABLE_USERS ($COLUMN_NOMBRE, $COLUMN_APELLIDO, $COLUMN_DNI, " +
                "$COLUMN_CELULAR, $COLUMN_EMAIL, $COLUMN_DIRECCION, $COLUMN_TIPO, $COLUMN_APTO) " +
                "VALUES ('Maria', 'Falsa', '123456788', '0000003', 'maria@falsa.com', 'Calle 12', 'No Socio', 1)")

        // Inserta cuota vencida para Ana (fecha pasada)
        db?.execSQL("""
        INSERT INTO cuotas (idSocio, monto, fechaPago, fechaVencimiento, estado)
        VALUES (1, 1500.0, '2024-05-01', '2024-06-01', 'Vencida')
        """.trimIndent())

        // Inserta cuota próxima a vencer para Juan (fecha futura)
                db?.execSQL("""
        INSERT INTO cuotas (idSocio, monto, fechaPago, fechaVencimiento, estado)
        VALUES (2, 1500.0, '2025-06-01', '2025-07-01', 'Activa')
        """.trimIndent())
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADMINS")
        db?.execSQL("DROP TABLE IF EXISTS actividades")
        db?.execSQL("DROP TABLE IF EXISTS actividad_pagos")
        db?.execSQL("DROP TABLE IF EXISTS cuotas")
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

    /*fun obtenerUsuarioPorDNI(dni: String): Map<String, String>? {
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
*/
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

    fun obtenerIdPorDNI(dni: String): Int? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM $TABLE_USERS WHERE dni = ?", arrayOf(dni))
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            cursor.close()
            id
        } else {
            cursor.close()
            null
        }
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
    /*fun insertarCuotaMensual(dni: String, monto: Double): Boolean {
        val db = this.writableDatabase

        try {
            // Obtener ID del socio desde su DNI
            val cursor = db.rawQuery("SELECT id FROM $TABLE_USERS WHERE dni = ?", arrayOf(dni))
            if (cursor.moveToFirst()) {
                val idSocio = cursor.getInt(0)
                cursor.close()

                val fechaHoy = LocalDate.now().toString()
                val fechaVencimiento = LocalDate.now().plusMonths(1).toString()

                val values = ContentValues().apply {
                    put("idSocio", idSocio)
                    put("monto", monto)
                    put("fechaPago", fechaHoy)
                    put("fechaVencimiento", fechaVencimiento)
                    put("estado", "Pagada")
                }

                val resultado = db.insert("cuotas", null, values)
                return resultado != -1L
            } else {
                cursor.close()
                return false
            }
        } catch (e: Exception) {
            return false
        }
    }
*/

    //Cuotas vencidas
    fun cuotasVencidas(): List<String> {
        val cuotas = mutableListOf<String>()
        val db = this.readableDatabase

        try {
            val query = """
            SELECT u.nombre, u.apellido, c.fecha_vencimiento
            FROM users u
            JOIN cuotas c ON u.id = c.id_usuario
            WHERE date(c.fecha_vencimiento) < date('now')
            AND c.pagado = 0
            ORDER BY c.fecha_vencimiento ASC
        """.trimIndent()

            val cursor = db.rawQuery(query, null)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val nombre = cursor.getString(0)
                    val apellido = cursor.getString(1)
                    val fecha = cursor.getString(2)
                    cuotas.add("$nombre $apellido - Venció el $fecha")
                } while (cursor.moveToNext())
                cursor.close()
            }

        } catch (e: Exception) {
            Log.e("DB", "Error en cuotasVencidas: ${e.message}")
        } finally {
            db.close()
        }

        return cuotas
    }

    fun obtenerCuotasVencidasOProximas(diasAdelante: Int = 0): List<String> {
        val cuotas = mutableListOf<String>()
        val db = this.readableDatabase

        val query = """
        SELECT u.nombre, u.apellido, c.fechaVencimiento, c.estado
        FROM cuotas c
        JOIN users u ON c.idSocio = u.id
        WHERE date(c.fechaVencimiento) <= date('now', '+$diasAdelante days')
        AND c.estado != 'Pagada'
        ORDER BY c.fechaVencimiento ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val apellido = cursor.getString(1)
                val fecha = cursor.getString(2)
                val estado = cursor.getString(3)
                cuotas.add("$nombre $apellido - $estado - Vence: $fecha")
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return cuotas
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

fun insertarPagoActividadPorNombre(dni: String, nombreActividad: String): Boolean {
    val db = writableDatabase

    val cursorUsuario = db.rawQuery("SELECT id FROM users WHERE dni = ?", arrayOf(dni))
    val cursorActividad = db.rawQuery("SELECT id FROM actividades WHERE nombre = ?", arrayOf(nombreActividad))

    if (!cursorUsuario.moveToFirst() || !cursorActividad.moveToFirst()) {
        cursorUsuario.close()
        cursorActividad.close()
        return false
    }

    val idUsuario = cursorUsuario.getInt(0)
    val idActividad = cursorActividad.getInt(0)
    val fechaActual = java.time.LocalDate.now().toString()

    cursorUsuario.close()
    cursorActividad.close()

    val values = ContentValues().apply {
        put("id_usuario", idUsuario)
        put("id_actividad", idActividad)
        put("fecha", fechaActual)
    }

    return db.insert("actividad_pagos", null, values) != -1L
}


    fun obtenerPagosActividadesPorDNI(dni: String): List<String> {
        val lista = mutableListOf<String>()
        val db = readableDatabase

        val query = """
        SELECT a.nombre, ap.fecha 
        FROM actividad_pagos ap
        JOIN users u ON u.id = ap.id_usuario
        JOIN actividades a ON a.id = ap.id_actividad
        WHERE u.dni = ?
        ORDER BY ap.fecha DESC
    """

        val cursor = db.rawQuery(query, arrayOf(dni))

        while (cursor.moveToNext()) {
            val actividad = cursor.getString(0)
            val fecha = cursor.getString(1)
            lista.add("Actividad: $actividad - Fecha: $fecha")
        }

        cursor.close()
        return lista
    }



    fun obtenerActividadesPorDNI(dni: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT actividad, fecha FROM PagosActividades WHERE dni = ?", arrayOf(dni))
        val actividades = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val actividad = cursor.getString(0)
            val fecha = cursor.getLong(1)
            actividades.add("Actividad: $actividad - Fecha: ${Date(fecha)}")
        }
        cursor.close()
        return actividades
    }

    /*fun obtenerVencimientoCuota(dni: String): String? {
        val db = this.readableDatabase

        val query = """
        SELECT c.fechaVencimiento
        FROM cuotas c
        JOIN socios s ON c.idSocio = s.id
        WHERE s.dni = ?
        ORDER BY c.fechaVencimiento DESC
        LIMIT 1
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(dni))

        return if (cursor.moveToFirst()) {
            val fecha = cursor.getString(0)
            cursor.close()
            fecha
        } else {
            cursor.close()
            null
        }
    }
*/

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
                "nombre" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                "apellido" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO)),
                "celular" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CELULAR)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                "direccion" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION)),
                "tipo" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                "aptoMedico" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APTO))
            )
            cursor.close()
            return usuario
        }
        cursor.close()
        return null
    }

    fun insertarCuotaMensual(dni: String): Boolean {
        val db = this.writableDatabase
        val montoFijo = 55000.0

        return try {
            // Buscar el ID del usuario por DNI
            val query = "SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_DNI = ?"
            val cursor = db.rawQuery(query, arrayOf(dni))

            if (cursor.moveToFirst()) {
                val idSocio = cursor.getInt(0)
                cursor.close()
                //Log.d("CUOTA_DEBUG", "Usuario encontrado, ID: $idSocio")

                // Preparar fechas
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fechaHoy = sdf.format(Date())

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, 1)
                val fechaVencimiento = sdf.format(calendar.time)

                // Insertar la cuota
                val values = ContentValues().apply {
                    put("idSocio", idSocio)
                    put("monto", montoFijo)
                    put("fechaPago", fechaHoy)
                    put("fechaVencimiento", fechaVencimiento)
                    put("estado", "Pagada")
                }

                val resultado = db.insert("cuotas", null, values)
                //Log.d("CUOTA_DEBUG", "Resultado inserción: $resultado")

                resultado != -1L

            } else {
                cursor.close()
                //Log.d("CUOTA_DEBUG", "Usuario NO encontrado con DNI: $dni")
                false
            }

        } catch (e: Exception) {
            //Log.e("CUOTA_DEBUG", "Excepción: ${e.message}")
            false
        }
    }

    fun obtenerVencimientoCuota(dni: String): String? {
        val db = this.readableDatabase
        Log.d("DBHelper", "Buscando vencimiento para DNI: $dni")

        try {
            val query = """
        SELECT c.fechaVencimiento
        FROM cuotas c
        JOIN $TABLE_USERS u ON c.idSocio = u.${COLUMN_ID}
        WHERE u.$COLUMN_DNI = ?
        ORDER BY c.fechaVencimiento DESC
        LIMIT 1
        """.trimIndent()

            Log.d("DBHelper", "Query: $query")
            val cursor = db.rawQuery(query, arrayOf(dni))

            return if (cursor.moveToFirst()) {
                val fecha = cursor.getString(0)
                Log.d("DBHelper", "Fecha vencimiento encontrada: $fecha")
                cursor.close()
                fecha
            } else {
                Log.d("DBHelper", "No se encontró vencimiento para DNI: $dni")
                cursor.close()
                null
            }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error al obtener vencimiento", e)
            return null
        }
    }


}

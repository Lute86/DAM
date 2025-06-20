package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class NewActivityActivity : BaseActivity() {

    private lateinit var editNombre: EditText
    private lateinit var editPrecio: EditText
    private lateinit var editCupo: EditText
    private lateinit var dbHelper: UserDBHelper

    private var isUpdate = false
    private var nombreOriginal = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_new_activity)

        editNombre = findViewById(R.id.editNombre)
        editPrecio = findViewById(R.id.editPrecio)
        editCupo = findViewById(R.id.editCupo)
        val btnSave = findViewById<Button>(R.id.btnGuardar)
        dbHelper = UserDBHelper(this)

        // Detectar si es edición
        isUpdate = intent.getBooleanExtra("isUpdate", false)
        if (isUpdate) {
            nombreOriginal = intent.getStringExtra("nombreActividad") ?: ""
            cargarDatos(nombreOriginal)
        }

        btnSave.setOnClickListener {
            guardarActividad()
        }
    }

    private fun cargarDatos(nombre: String) {
        val actividad = dbHelper.obtenerActividadPorNombre(nombre)
        if (actividad != null) {
            editNombre.setText(actividad["nombre"])
            editPrecio.setText(actividad["precio"])
            editCupo.setText(actividad["cupo"])
        } else {
            Toast.makeText(this, "Error al cargar actividad", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun guardarActividad() {
        val nombre = editNombre.text.toString().trim()
        val precioStr = editPrecio.text.toString().trim()
        val cupoStr = editCupo.text.toString().trim()

        if (nombre.isEmpty() || precioStr.isEmpty() || cupoStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        val cupo = cupoStr.toIntOrNull()

        if (precio == null || cupo == null) {
            Toast.makeText(this, "Precio y cupo deben ser numéricos", Toast.LENGTH_SHORT).show()
            return
        }

        if (isUpdate) {
            val result = dbHelper.actualizarActividad(nombreOriginal, nombre, precio, cupo)
            if (result > 0) {
                Toast.makeText(this, "Actividad actualizada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        } else {
            val result = dbHelper.insertarActividad(nombre, precio, cupo)
            if (result != -1L) {
                Toast.makeText(this, "Actividad registrada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

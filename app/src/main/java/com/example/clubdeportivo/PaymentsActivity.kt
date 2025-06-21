package com.example.clubdeportivo

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PaymentsActivity : BaseActivity() {

    private lateinit var spinnerServicio: Spinner
    private lateinit var editTextPrecio: EditText
    private lateinit var btnCobrar: Button
    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_payments)

        spinnerServicio = findViewById(R.id.spinnerServicio)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        btnCobrar = findViewById(R.id.btnCobrar)

        dbHelper = UserDBHelper(this)

        val isSocio = intent.getBooleanExtra("isSocio", false)
        val dni = intent.getStringExtra("dni")

        if (isSocio) {
            setupSocioOptions()
        } else {
            setupNoSocioOptions()
        }

        spinnerServicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                if (position == 0) {
                    // Seleccione una actividad, vacío y editable
                    editTextPrecio.setText("")
                    editTextPrecio.isEnabled = false
                } else {
                    // Mostrar precio según selección
                    val actividades = dbHelper.obtenerTodasLasActividades()
                    val seleccionada = actividades[position - 1] // porque el 0 es "Seleccione"
                    editTextPrecio.setText(seleccionada["precio"])
                    editTextPrecio.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nada
            }
        }

        btnCobrar.setOnClickListener {
            if (dni.isNullOrEmpty()) {
                Toast.makeText(this, "No se recibió el DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isSocio) {
                // Cobro cuota fija socio
                val exito = dbHelper.insertarCuotaMensual(dni)
                if (exito) {
                    Toast.makeText(this, "Cuota mensual cobrada exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al cobrar cuota mensual", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Cobro actividad no socio
                val pos = spinnerServicio.selectedItemPosition
                if (pos <= 0) {
                    Toast.makeText(this, "Seleccione una actividad válida", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val itemSeleccionado = spinnerServicio.selectedItem.toString()
                val nombreActividad = itemSeleccionado.split(" - ")[0]

                val exito = dbHelper.insertarPagoActividadPorNombre(dni, nombreActividad)
                if (exito) {
                    Toast.makeText(this, "Pago de actividad registrado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar pago. Verifique datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSocioOptions() {
        spinnerServicio.visibility = View.GONE
        editTextPrecio.visibility = View.VISIBLE

        val montoMensual = 55000.0
        editTextPrecio.setText(montoMensual.toString())
        editTextPrecio.isEnabled = false
    }

    private fun setupNoSocioOptions() {
        spinnerServicio.visibility = View.VISIBLE
        editTextPrecio.visibility = View.VISIBLE
        editTextPrecio.setText("")
        editTextPrecio.isEnabled = false

        val actividadesList = dbHelper.obtenerTodasLasActividades()
        val actividadesConPrecio = listOf("Seleccione una actividad") + actividadesList.map {
            val nombre = it["nombre"] ?: ""
            val precio = it["precio"] ?: "0"
            "$nombre - $precio ARS"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividadesConPrecio)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServicio.adapter = adapter
    }
}

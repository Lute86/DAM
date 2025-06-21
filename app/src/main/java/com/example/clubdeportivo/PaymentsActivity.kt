package com.example.clubdeportivo

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PaymentsActivity : BaseActivity() {

    private lateinit var spinnerServicio: Spinner
    private lateinit var spinnerTipoPago: Spinner
    private lateinit var editTextPrecio: EditText
    private lateinit var btnCobrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_payments)

        spinnerServicio = findViewById(R.id.spinnerServicio)
        spinnerTipoPago = findViewById(R.id.spinnerTipoPago)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        btnCobrar = findViewById(R.id.btnCobrar)

        val isSocio = intent.getBooleanExtra("isSocio", false)

        if (isSocio) {
            setupSocioOptions()
        } else {
            setupNoSocioOptions()
        }

        btnCobrar.setOnClickListener { realizarCobro() }
    }

    private fun setupSocioOptions() {
        spinnerServicio.visibility = View.GONE
        spinnerTipoPago.visibility = View.GONE

        // Fijar monto de la cuota mensual
        val montoMensual = 55000.0
        editTextPrecio.setText(montoMensual.toString())
        editTextPrecio.isEnabled = false // No editable
    }

    private fun setupNoSocioOptions() {
        spinnerServicio.visibility = View.VISIBLE

        val actividades = listOf("Seleccione una actividad", "Fútbol", "Natación", "Tenis")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServicio.adapter = adapter
    }

    private fun realizarCobro() {
        try {
            val dni = intent.getStringExtra("dni")
            if (dni.isNullOrEmpty()) {
                Toast.makeText(this, "No se recibió el DNI del socio", Toast.LENGTH_SHORT).show()
                return
            }

            val dbHelper = UserDBHelper(this)
            val exito = dbHelper.insertarCuotaMensual(dni)

            if (exito) {
                Toast.makeText(this, "Cuota mensual cobrada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al cobrar la cuota. Verifique el DNI o intente nuevamente.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
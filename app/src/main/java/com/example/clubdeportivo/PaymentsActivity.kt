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

        val periodos = listOf("Seleccione un periodo", "Mensual", "Anual")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoPago.adapter = adapter
    }

    private fun setupNoSocioOptions() {
        spinnerServicio.visibility = View.VISIBLE

        val actividades = listOf("Seleccione una actividad", "Fútbol", "Natación", "Tenis")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServicio.adapter = adapter
    }

    private fun realizarCobro() {
        if (spinnerServicio.visibility == View.VISIBLE) {
            val actividadSeleccionada = spinnerServicio.selectedItem as? String
            if (actividadSeleccionada == "Seleccione una actividad") {
                Toast.makeText(this, "Seleccione una actividad válida", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val precio = editTextPrecio.text.toString().toDoubleOrNull()
        if (precio == null || precio <= 0) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Cobro realizado: $precio", Toast.LENGTH_SHORT).show()
    }
}

package com.example.clubdeportivo

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PaymentsActivity : AppCompatActivity() {

    private lateinit var spinnerServicio: Spinner
    private lateinit var spinnerTipoPago: Spinner
    private lateinit var editTextPrecio: EditText
    private lateinit var btnCobrar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)

        spinnerServicio = findViewById(R.id.spinnerServicio)
        spinnerTipoPago = findViewById(R.id.spinnerTipoPago)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        btnCobrar = findViewById(R.id.btnCobrar)
        btnVolver = findViewById(R.id.btnVolver)

        val isSocio = intent.getBooleanExtra("isSocio", false)

        if (isSocio) {
            setupSocioOptions()
        } else {
            setupNoSocioOptions()
        }

        //btnCobrar.setOnClickListener { realizarCobro() }
        btnVolver.setOnClickListener { finish() }
    }

    private fun setupSocioOptions() {
        spinnerServicio.visibility = View.GONE
        spinnerTipoPago.prompt = "Periodo"
    }

    private fun setupNoSocioOptions() {
        spinnerServicio.visibility = View.VISIBLE
        spinnerServicio.prompt = "Elegir Actividad"
    }
/*
    private fun realizarCobro() {
        val precio = editTextPrecio.text.toString().toDoubleOrNull()
        if (precio == null || precio <= 0) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Aquí puedes manejar el cobro según el tipo
        Toast.makeText(this, "Cobro realizado: $precio", Toast.LENGTH_SHORT).show()
    }*/
}

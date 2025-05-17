package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoSociosMenuActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_no_socios_menu)

        val textDNI = findViewById<EditText>(R.id.textDNI)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnCobrar = findViewById<Button>(R.id.btnCobrar)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnActividades = findViewById<Button>(R.id.btnActividades)

        /*btnBuscar.setOnClickListener {
            //buscar en BD (valor textDNI) e ir a pantalla no socio
        }*/

        btnCobrar.setOnClickListener {
            val intent = Intent(this, PaymentsActivity::class.java)
            intent.putExtra("isSocio", false)
            startActivity(intent)
        }

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish() // Cierra la actividad actual y vuelve a la anterior
        }
        /*btnActualizar.setOnClickListener{
            //pantalla actualizar no socios
        }*/

        /*btnActividades.setOnClickListener{
            //pantalla actividades no socios
        }*/
    }
}
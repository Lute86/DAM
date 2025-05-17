package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class SociosMenuActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_socios_menu)

        val textDNI = findViewById<EditText>(R.id.textDNI)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnCobrar = findViewById<Button>(R.id.btnCobrar)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnInfo = findViewById<Button>(R.id.btnInfo)

        /*btnBuscar.setOnClickListener {
            //buscar en BD con valor de textDNI e ir a pantalla socio
        }*/

        btnCobrar.setOnClickListener {
            val intent = Intent(this, PaymentsActivity::class.java)
            intent.putExtra("isSocio", true)
            startActivity(intent)
        }
        /*
        val btnLista = findViewById<Button>(R.id.btnLista)
        btnLista.setOnClickListener {
            val intent = Intent(this, ListaSocioActivity::class.java)
            startActivity(intent)
        }*/

        /*btnActualizar.setOnClickListener{
            //pantalla actualizar socios
        }*/

        /*btnInfo.setOnClickListener{
            //pantalla info socios
        }*/
    }
}
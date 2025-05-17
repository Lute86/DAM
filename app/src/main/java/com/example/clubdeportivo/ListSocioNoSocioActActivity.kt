package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class ListSocioNoSocioActActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_list_socio_nosocio)

        // Configurar botones con un solo método
        setupButton(R.id.btnListSocios, "socios")
        setupButton(R.id.btnListNoSocios, "noSocios")
        setupButton(R.id.btnListAtivities, "Actividades")
    }

    // Método para configurar botones
    private fun setupButton(buttonId: Int, tipo: String) {
        val button = findViewById<Button>(buttonId)
        button.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("tipo", tipo)
            startActivity(intent)
        }
    }
}

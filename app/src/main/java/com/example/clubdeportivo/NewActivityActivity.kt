package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NewActivityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_activity)

        val btnSave = findViewById<Button>(R.id.btnGuardar)
        val btnBack = findViewById<Button>(R.id.btnVolver)

        btnSave.setOnClickListener {
            // Guardar nueva actividad
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
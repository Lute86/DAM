package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NewActivityActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_new_activity)

        val btnSave = findViewById<Button>(R.id.btnGuardar)

        btnSave.setOnClickListener {
            // Guardar nueva actividad
        }

    }
}
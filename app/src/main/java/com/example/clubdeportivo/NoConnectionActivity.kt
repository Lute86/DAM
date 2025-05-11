package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)

        val btnRetry = findViewById<Button>(R.id.btnRetry)
        val textViewErrorDetails = findViewById<TextView>(R.id.textViewErrorDetails)
        val imageViewError = findViewById<ImageView>(R.id.imageViewError)

        val isServerError = intent.getBooleanExtra("IS_SERVER_ERROR", false)

        if (isServerError) {
            textViewErrorDetails.text = "Problemas técnicos. Intenta luego"
            imageViewError.setImageResource(R.drawable.technical_background)
        } else {
            textViewErrorDetails.text = "Revisa tu conexión"
            imageViewError.setImageResource(R.drawable.connection_background)
        }

        btnRetry.setOnClickListener {
            if (hasInternetConnection()) {
                finish() // Vuelve a la pantalla anterior
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        //logica conexion
        return false;
    }
}

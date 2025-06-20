package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ClientsMenuActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_clients_menu)
        // setContentView(R.layout.activity_clients_menu)

        val btnSocios = findViewById<Button>(R.id.btnSocios)
        val btnNoSocios = findViewById<Button>(R.id.btnNoSocios)
       // val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)


        btnSocios.setOnClickListener {
            val intent = Intent(this, SociosMenuActivity::class.java)
            startActivity(intent)
        }

        btnNoSocios.setOnClickListener {
            val intent = Intent(this, NoSociosMenuActivity::class.java)
            startActivity(intent)
        }

        val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)
        btnVencimientos.setOnClickListener {
            val intent = Intent(this, ExpirationsActivity::class.java)
            startActivity(intent)
        }

        /*btnVencimientos.setOnClickListener {
            val intent = Intent(this, ExpirationListActivity::class.java)
            startActivity(intent)
        }*/
    }
}
package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainMenuActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_main_menu)
        //setContentView(R.layout.activity_main_menu)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val btnClient = findViewById<Button>(R.id.btnClient)
        btnClient.setOnClickListener {
            val intent = Intent(this, ClientsMenuActivity::class.java)
            startActivity(intent)
        }

        val btnList = findViewById<Button>(R.id.btnList)
        btnList.setOnClickListener {
            val intent = Intent(this, ListSocioNoSocioActActivity::class.java)
            startActivity(intent)
        }
        val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)
        btnVencimientos.setOnClickListener {
            val intent = Intent(this, ExpirationsActivity::class.java)
            startActivity(intent)
        }
    }
}
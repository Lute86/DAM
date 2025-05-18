package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()
        setActivityLayout(R.layout.activity_register)

        val btnRegistPerson = findViewById<Button>(R.id.btnRegistPerson)
        val btnRegistActivity = findViewById<Button>(R.id.btnRegistActivity)

        btnRegistPerson.setOnClickListener {
            val intent = Intent(this, RegisterUpdateActivity::class.java)
            startActivity(intent)
        }

        btnRegistActivity.setOnClickListener {
            val intent = Intent(this, NewActivityActivity::class.java)
            startActivity(intent)
        }

    }
}
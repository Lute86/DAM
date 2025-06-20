package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = UserDBHelper(this)
        dbHelper.writableDatabase // Fuerza la creación de la base de datos

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val showPasswordCheckBox = findViewById<CheckBox>(R.id.checkboxShowPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Mostrar/Ocultar contraseña
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Mueve el cursor al final para que no se reinicie
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Botón de Login
        btnLogin.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            val isValid = dbHelper.verificarLoginAdmin(username, password)
            if (isValid) {
                startActivity(Intent(this, MainMenuActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

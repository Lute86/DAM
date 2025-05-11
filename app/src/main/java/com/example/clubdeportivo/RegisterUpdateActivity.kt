package com.example.clubdeportivo
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterUpdateActivity : AppCompatActivity() {
    private lateinit var titleText: TextView
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextDNI: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var checkSocio: CheckBox
    private lateinit var checkNoSocio: CheckBox
    private lateinit var checkAptaMedica: CheckBox
    private lateinit var btnGuardar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_update)

        titleText = findViewById(R.id.titleText)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextDNI = findViewById(R.id.editTextDNI)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        checkSocio = findViewById(R.id.checkSocio)
        checkNoSocio = findViewById(R.id.checkNoSocio)
        checkAptaMedica = findViewById(R.id.checkAptaMedica)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnVolver = findViewById<Button>(R.id.btnVolver)

        val isUpdate = intent.getBooleanExtra("isUpdate", false)
        setupUI(isUpdate)

        btnGuardar.setOnClickListener { handleSave(isUpdate) }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun setupUI(isUpdate: Boolean) {
        if (isUpdate) {
            titleText.text = "Actualizar"
            btnGuardar.text = "Actualizar"
        } else {
            titleText.text = "Registrar"
            btnGuardar.text = "Registrar"
        }
    }

    private fun handleSave(isUpdate: Boolean) {
        if (isUpdate) {
            // Lógica para actualizar
        } else {
            // Lógica para registrar
        }
    }
}

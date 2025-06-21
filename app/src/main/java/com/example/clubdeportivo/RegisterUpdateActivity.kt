package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterUpdateActivity : BaseActivity() {

    private lateinit var titleText: TextView
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextDNI: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextNumCelular: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var checkSocio: CheckBox
    private lateinit var checkNoSocio: CheckBox
    private lateinit var checkAptaMedica: CheckBox
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_register_update)

        titleText = findViewById(R.id.titleText)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextDNI = findViewById(R.id.editTextDNI)
        editTextNumCelular = findViewById(R.id.editTextNumCelular)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        checkSocio = findViewById(R.id.checkSocio)
        checkNoSocio = findViewById(R.id.checkNoSocio)
        checkAptaMedica = findViewById(R.id.checkAptaMedica)
        btnGuardar = findViewById(R.id.btnGuardar)

        val isUpdate = intent.getBooleanExtra("isUpdate", false)
        setupUI(isUpdate)

        checkSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkNoSocio.isChecked = false
        }

        checkNoSocio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkSocio.isChecked = false
        }

        btnGuardar.setOnClickListener { handleSave(isUpdate) }
    }

    private fun setupUI(isUpdate: Boolean) {
        if (isUpdate) {
            titleText.text = "Actualizar"
            btnGuardar.text = "Actualizar"

            val dni = intent.getStringExtra("dni") ?: return
            val dbHelper = UserDBHelper(this)
            val usuario = dbHelper.obtenerUsuarioPorDNI(dni)

            usuario?.let {
                editTextDNI.setText(dni)
                editTextDNI.isEnabled = false
                editTextNombre.setText(it["nombre"])
                editTextApellido.setText(it["apellido"])
                editTextNumCelular.setText(it["celular"])
                editTextEmail.setText(it["email"])
                editTextDireccion.setText(it["direccion"])
                checkSocio.isChecked = it["tipo"] == "Socio"
                checkNoSocio.isChecked = it["tipo"] == "No Socio"
                checkAptaMedica.isChecked = it["aptoMedico"] == "1"
            }
        } else {
            titleText.text = "Registrar"
            btnGuardar.text = "Registrar"
        }
    }

    private fun handleSave(isUpdate: Boolean) {
        val nombre = editTextNombre.text.toString().trim()
        val apellido = editTextApellido.text.toString().trim()
        val dni = editTextDNI.text.toString().trim()
        val celular = editTextNumCelular.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val direccion = editTextDireccion.text.toString().trim()
        val esSocio = checkSocio.isChecked
        val esNoSocio = checkNoSocio.isChecked
        val aptoMedico = checkAptaMedica.isChecked

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || celular.isEmpty() || email.isEmpty() || direccion.isEmpty()) {
            showToast("Por favor complete todos los campos.")
            return
        }

        if (esSocio == esNoSocio) {
            showToast("Seleccione solo una opción: Socio o No Socio.")
            return
        }

        if (!aptoMedico) {
            showToast("Debe tener el apto médico para continuar.")
            return
        }

        val tipo = if (esSocio) "Socio" else "No Socio"
        val dbHelper = UserDBHelper(this)

        if (isUpdate) {
            val filasActualizadas = dbHelper.actualizarUsuario(dni, nombre, apellido, celular, email, direccion, tipo, true)
            if (filasActualizadas > 0) {
                showToast("Usuario actualizado correctamente.")
                finish()
            } else {
                showToast("No se encontró un usuario con ese DNI.")
            }
        } else {
            val resultado = dbHelper.insertarUsuario(nombre, apellido, dni, celular, email, direccion, tipo, true)
            if (resultado != -1L) {
                showToast("Usuario registrado correctamente.")
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                limpiarCampos()

            } else {
                showToast("Error: el DNI '$dni' ya está registrado.")
            }
        }
    }

    private fun showToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun limpiarCampos() {
        editTextNombre.text.clear()
        editTextApellido.text.clear()
        editTextDNI.text.clear()
        editTextDireccion.text.clear()
        editTextEmail.text.clear()
        editTextNumCelular.text.clear()
        checkSocio.isChecked = false
        checkNoSocio.isChecked = false
        checkAptaMedica.isChecked = false
    }
}

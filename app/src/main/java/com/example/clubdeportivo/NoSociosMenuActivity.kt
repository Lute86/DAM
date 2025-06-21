package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity

class NoSociosMenuActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_no_socios_menu)

        val textDNI = findViewById<EditText>(R.id.textDNI)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnCobrar = findViewById<Button>(R.id.btnCobrar)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnActividades = findViewById<Button>(R.id.btnActividades)
        val textNoSocioInfo = findViewById<TextView>(R.id.textNoSocioInfo)

        var dniIngresado: String? = null
        btnCobrar.isEnabled = false
        btnActualizar.isEnabled = false
        btnActividades.isEnabled = false

        // Funcionalidad del botón Buscar
        btnBuscar.setOnClickListener {
            val dni = textDNI.text.toString()
            if (dni.isNotEmpty()) {
                val dbHelper = UserDBHelper(this)
                val noSocio = dbHelper.obtenerUsuarioPorDNI(dni)

                if (noSocio != null && noSocio["tipo"] == "No Socio") {
                    dniIngresado = dni
                    val infoView = findViewById<TextView>(R.id.textNoSocioInfo)
                    infoView.visibility = View.VISIBLE
                    infoView.text = "No Socio: ${noSocio["nombre"]} ${noSocio["apellido"]}"
                    // textSocioInfo.text = info
                    textNoSocioInfo.visibility = View.VISIBLE
                    btnCobrar.isEnabled = true
                    btnActualizar.isEnabled = true
                    btnActividades.isEnabled = true
                    // Opcional: deshabilitar campo DNI si querés que no se cambie
                    textDNI.isEnabled = false
                } else {
                    Toast.makeText(this, "No Socio no encontrado", Toast.LENGTH_SHORT).show()
                    textNoSocioInfo.text = ""
                    textNoSocioInfo.visibility = View.GONE
                }
            } else {
                Toast.makeText(this, "Ingrese un DNI válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Funcionalidad del botón Cobrar
        btnCobrar.setOnClickListener {
            val dni = textDNI.text.toString()
            val intent = Intent(this, PaymentsActivity::class.java)
            intent.putExtra("dni", dni)  // clave en minúsculas o como la recibas
            intent.putExtra("isNoSocio", true) // Indica que se trata de un socio
            startActivity(intent)
        }

        // Funcionalidad del botón Actualizar
        btnActualizar.setOnClickListener {
            val dni = textDNI.text.toString()
            if (dni.isNotEmpty()) {
                val intent = Intent(this, RegisterUpdateActivity::class.java)
                intent.putExtra("isUpdate",true) // Indica que es una operación de actualización
                intent.putExtra("dni", dni)
                startActivity(intent)
                btnBuscar.performClick()
            }
        }

        // Funcionalidad del botón list actividades realizadas
        btnActividades.setOnClickListener {
            val dni = textDNI.text.toString()
            if (dni.isNotEmpty()) {
                val intent = Intent(this, ListaPagosActivity::class.java)
                intent.putExtra("DNI", dni)  // Pasamos el DNI con la clave "DNI"
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ingrese un DNI para ver la información", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Si el campo DNI está cargado y deshabilitado, volver a mostrar datos
        val textDNI = findViewById<EditText>(R.id.textDNI)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)

        if (!textDNI.isEnabled && textDNI.text.isNotEmpty()) {
            btnBuscar.performClick()
        }
    }
}
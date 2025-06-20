package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class SociosMenuActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_socios_menu)

        val textDNI = findViewById<EditText>(R.id.textDNI)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnCobrar = findViewById<Button>(R.id.btnCobrar)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnInfo = findViewById<Button>(R.id.btnInfo)
        val textSocioInfo = findViewById<TextView>(R.id.textSocioInfo)

        var dniIngresado: String? = null
        btnCobrar.isEnabled = false
        btnActualizar.isEnabled = false
        btnInfo.isEnabled = false


        // Funcionalidad del botón Buscar
        btnBuscar.setOnClickListener {
            val dni = textDNI.text.toString()
            if (dni.isNotEmpty()) {
                val dbHelper = UserDBHelper(this)
                val socio = dbHelper.obtenerUsuarioPorDNI(dni)

                if (socio != null) {/*
                    val nombre = socio["nombre"]
                    val apellido = socio["apellido"]
                    val tipo = socio["tipo"]
                    val celular = socio["celular"]
                    val email = socio["email"]
                    val direccion = socio["direccion"]

                    val info = """
                Nombre: $nombre $apellido
                Tipo: $tipo
                Celular: $celular
                Email: $email
                Dirección: $direccion
            """.trimIndent()
*/
                    val infoView = findViewById<TextView>(R.id.textSocioInfo)
                    infoView.visibility = View.VISIBLE
                    infoView.text = "Socio: ${socio["nombre"]} ${socio["apellido"]}"
                   // textSocioInfo.text = info
                    textSocioInfo.visibility = View.VISIBLE
                    btnCobrar.isEnabled = true
                    btnActualizar.isEnabled = true
                    btnInfo.isEnabled = true
                    // Opcional: deshabilitar campo DNI si querés que no se cambie
                    textDNI.isEnabled = false
                } else {
                    Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
                    textSocioInfo.text = ""
                    textSocioInfo.visibility = View.GONE
                }
            } else {
                Toast.makeText(this, "Ingrese un DNI válido", Toast.LENGTH_SHORT).show()
            }


            // Funcionalidad del botón Cobrar
            btnCobrar.setOnClickListener {
                dniIngresado?.let {
                    val intent = Intent(this, PaymentsActivity::class.java)
                    intent.putExtra("dni", it)
                    startActivity(intent)
                } ?: Toast.makeText(this, "Primero busque un socio válido", Toast.LENGTH_SHORT)
                    .show()
            }

            // Funcionalidad del botón Actualizar
            btnActualizar.setOnClickListener {
                val dni = textDNI.text.toString()
                if (dni.isNotEmpty()) {
                    val intent = Intent(this, RegisterUpdateActivity::class.java)
                    intent.putExtra(
                        "isUpdate",
                        true
                    ) // Indica que es una operación de actualización
                    intent.putExtra("dni", dni)
                    startActivity(intent)
                    btnBuscar.performClick()
                }
            }


            // Funcionalidad del botón Info
            btnInfo.setOnClickListener {
                dniIngresado?.let {
                    val intent = Intent(this, ListaPagosActivity::class.java)
                    intent.putExtra("DNI", it)
                    startActivity(intent)
                } ?: Toast.makeText(this, "Primero busque un socio válido", Toast.LENGTH_SHORT)
                    .show()
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
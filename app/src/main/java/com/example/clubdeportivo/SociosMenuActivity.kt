package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
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

        // Funcionalidad del botón Buscar
        btnBuscar.setOnClickListener {
            val dni = textDNI.text.toString()
            if (dni.isNotEmpty()) {
                // Lógica para buscar en la base de datos o mostrar datos del socio
                Toast.makeText(this, "Buscando socio con DNI: $dni", Toast.LENGTH_SHORT).show()
                // Aquí puedes agregar la lógica para abrir una pantalla de detalles del socio
            } else {
                Toast.makeText(this, "Ingrese un DNI válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Funcionalidad del botón Cobrar
        btnCobrar.setOnClickListener {
            val intent = Intent(this, PaymentsActivity::class.java)
            intent.putExtra("isSocio", true) // Indica que se trata de un socio
            startActivity(intent)
        }

        // Funcionalidad del botón Actualizar
        btnActualizar.setOnClickListener {
            val dni = textDNI.text.toString()
            if (dni.isNotEmpty()) {
                val intent = Intent(this, RegisterUpdateActivity::class.java)
                intent.putExtra("isUpdate", true) // Indica que es una operación de actualización
                intent.putExtra("DNI", dni)      // Pasa el DNI al siguiente Activity
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ingrese un DNI para actualizar", Toast.LENGTH_SHORT).show()
            }
        }


        // Funcionalidad del botón Info
        btnInfo.setOnClickListener {
            val dni = textDNI.text.toString()


            if (dni.isNotEmpty()) {
                val intent = Intent(this, ListaPagosActivity::class.java)
                intent.putExtra("DNI", dni)  // Pasamos el DNI con la clave "DNI"
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ingrese un DNI para ver la información", Toast.LENGTH_SHORT).show()
            }
        }

        /*btnBuscar.setOnClickListener {
            //buscar en BD con valor de textDNI e ir a pantalla socio
        }

        btnCobrar.setOnClickListener {
            val intent = Intent(this, PaymentsActivity::class.java)
            intent.putExtra("isSocio", true)
            startActivity(intent)
        }
        /
        val btnLista = findViewById<Button>(R.id.btnLista)
        btnLista.setOnClickListener {
            val intent = Intent(this, ListaSocioActivity::class.java)
            startActivity(intent)
        }*/

        /*btnActualizar.setOnClickListener{
            //pantalla actualizar socios
        }*/

        /*btnInfo.setOnClickListener{
            //pantalla info socios
        }*/
    }
}
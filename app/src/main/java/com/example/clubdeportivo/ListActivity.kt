package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListActivity : BaseActivity() {

    private lateinit var dbHelper: UserDBHelper
    private lateinit var tipo: String
    private lateinit var adapter: ListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_list)

        dbHelper = UserDBHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tipo = intent.getStringExtra("tipo") ?: "Desconocido"
        findViewById<TextView>(R.id.textTitulo).text = "Lista de $tipo"

        cargarDatos()
    }

    private fun cargarDatos() {
        val encabezado: String
        val datos: List<String>

        when (tipo.lowercase()) {
            "socios", "no socios" -> {
                encabezado = String.format("%-30s | %-15s", "Nombre y Apellido", "DNI")
                val tipoUsuario = if (tipo.lowercase() == "socios") "Socio" else "No Socio"
                val lista = dbHelper.obtenerUsuariosPorTipo(tipoUsuario).map {
                    val nombreCompleto = "${it["nombre"]} ${it["apellido"]}"
                    "$nombreCompleto | ${it["dni"]}"
                }
                datos = listOf(encabezado) + lista
            }

            "actividades" -> {
                encabezado = String.format("%-15s | %-8s | %-5s", "Nombre", "Precio", "Cupo")
                val actividades = dbHelper.obtenerTodasLasActividades()
                datos = listOf(encabezado) + actividades.map {
                    String.format("%-15s | %-8s | %-5s", it["nombre"], it["precio"], it["cupo"])
                }
            }

            else -> {
                encabezado = "Sin datos disponibles para '$tipo'"
                datos = listOf(encabezado)
            }
        }

        adapter = ListAdapter(datos, object : OnItemClickListener {
            override fun onItemClick(item: String, position: Int) {
                if (position == 0) return

                try {
                    when (tipo.lowercase()) {
                        "socios", "no socios" -> {
                            val parts = item.split("|")
                            if (parts.size < 2) return
                            val dni = parts[1].trim()
                            val usuario = dbHelper.obtenerUsuarioPorDNI(dni)

                            usuario?.let {
                                val mensaje = """
                                    Nombre: ${it["nombre"]}
                                    Apellido: ${it["apellido"]}
                                    DNI: $dni
                                    Celular: ${it["celular"]}
                                    Email: ${it["email"]}
                                    Dirección: ${it["direccion"]}
                                    Tipo: ${it["tipo"]}
                                    Apto Médico: ${if (it["aptoMedico"] == "1") "Sí" else "No"}
                                """.trimIndent()

                                android.app.AlertDialog.Builder(this@ListActivity)
                                    .setTitle("Detalles del usuario")
                                    .setMessage(mensaje)
                                    .setPositiveButton("Modificar") { _, _ ->
                                        startActivity(
                                            Intent(this@ListActivity, RegisterUpdateActivity::class.java)
                                                .putExtra("isUpdate", true)
                                                .putExtra("dni", dni)
                                        )
                                    }
                                    .setNegativeButton("Eliminar") { _, _ ->
                                        android.app.AlertDialog.Builder(this@ListActivity)
                                            .setTitle("Confirmar eliminación")
                                            .setMessage("¿Seguro que querés eliminar a esta persona?")
                                            .setPositiveButton("Sí") { _, _ ->
                                                dbHelper.eliminarUsuarioPorDNI(dni)
                                                cargarDatos()
                                            }
                                            .setNegativeButton("No", null)
                                            .show()
                                    }
                                    .setNeutralButton("Cancelar", null)
                                    .show()
                            }
                        }

                        "actividades" -> {
                            val parts = item.split("|")
                            if (parts.size < 1) return
                            val nombreActividad = parts[0].trim()
                            val actividad = dbHelper.obtenerTodasLasActividades()
                                .find { it["nombre"]?.trim() == nombreActividad }

                            actividad?.let {
                                val mensaje = """
                                    Nombre: ${it["nombre"]}
                                    Precio: $${it["precio"]}
                                    Cupo: ${it["cupo"]}
                                """.trimIndent()

                                android.app.AlertDialog.Builder(this@ListActivity)
                                    .setTitle("Detalles de actividad")
                                    .setMessage(mensaje)
                                    .setPositiveButton("Modificar") { _, _ ->
                                        startActivity(
                                            Intent(this@ListActivity, NewActivityActivity::class.java)
                                                .putExtra("isUpdate", true)
                                                .putExtra("nombreActividad", it["nombre"])
                                        )
                                    }
                                    .setNegativeButton("Eliminar") { _, _ ->
                                        android.app.AlertDialog.Builder(this@ListActivity)
                                            .setTitle("Confirmar eliminación")
                                            .setMessage("¿Seguro que querés eliminar la actividad '${it["nombre"]}'?")
                                            .setPositiveButton("Sí") { _, _ ->
                                                dbHelper.eliminarActividadPorNombre(nombreActividad)
                                                cargarDatos()
                                            }
                                            .setNegativeButton("No", null)
                                            .show()
                                    }
                                    .setNeutralButton("Cancelar", null)
                                    .show()
                            }
                        }

                        else -> Toast.makeText(this@ListActivity, "Acción no soportada", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ListActivity", "Error al procesar clic: ${e.message}")
                    Toast.makeText(this@ListActivity, "Error al procesar elemento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onItemLongClick(item: String, position: Int) {
                Toast.makeText(this@ListActivity, "Long click: $item", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarDatos() // Actualiza sin recrear toda la Activity
    }
}

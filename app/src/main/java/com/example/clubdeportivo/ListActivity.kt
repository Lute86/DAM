package com.example.clubdeportivo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListActivity : BaseActivity(), OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val tipo = intent.getStringExtra("tipo") ?: "Desconocido"

        val textTitulo = findViewById<TextView>(R.id.textTitulo)
        textTitulo.text = "Lista de $tipo"

        val datos = when (tipo) {
            "socios" -> listOf("Socio 1", "Socio 2", "Socio 3")
            "noSocios" -> listOf("No Socio 1", "No Socio 2")
            "Actividades" -> listOf("Fútbol", "Natación", "Yoga")
            else -> listOf("Sin datos")
        }

        val adapter = ListAdapter(datos, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(item: String) {
        Toast.makeText(this, "Seleccionado: $item", Toast.LENGTH_SHORT).show()
    }
}

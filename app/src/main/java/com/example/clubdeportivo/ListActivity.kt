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

    class ListActivity : BaseActivity(), OnItemClickListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setActivityLayout(R.layout.activity_list)

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)

            val dbHelper = UserDBHelper(this)
            val actividades = dbHelper.obtenerTodasLasActividades()

            val textTitulo = findViewById<TextView>(R.id.textTitulo)
            textTitulo.text = "Actividades disponibles"

            val adapter = ListAdapter(actividades, this)
            recyclerView.adapter = adapter
        }

        override fun onItemClick(item: String) {
            Toast.makeText(this, "Seleccionado: $item", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onItemClick(item: String) {
        Toast.makeText(this, "Seleccionado: $item", Toast.LENGTH_SHORT).show()
    }
}

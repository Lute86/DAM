package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpirationsActivity : BaseActivity(), OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_expirations)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val vencimientos = listOf(
            "Socio 1 Pago de Cuota - 15/05/2025",
            "Socio 2 Renovación Anual - 01/06/2025"
        )

        val adapter = ListAdapter(vencimientos, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(item: String) {
        Toast.makeText(this, "Seleccionado: $item", Toast.LENGTH_SHORT).show()
    }
}

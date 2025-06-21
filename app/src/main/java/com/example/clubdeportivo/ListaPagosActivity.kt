package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaPagosActivity : BaseActivity(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListAdapter
    private var pagosList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_lista_pagos)

        recyclerView = findViewById(R.id.recyclerViewPagos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dni = intent.getStringExtra("DNI")
        if (dni.isNullOrEmpty()) {
            Toast.makeText(this, "No se recibió DNI", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = UserDBHelper(this)
        pagosList = dbHelper.obtenerPagosActividadesPorDNI(dni)

        adapter = ListAdapter(pagosList, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(item: String, position: Int) {
        Toast.makeText(this, "Seleccionaste: $item", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(item: String, position: Int) {
        // Implementar si querés opciones de largo click
    }
}

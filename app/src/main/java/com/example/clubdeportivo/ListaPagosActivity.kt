package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaPagosActivity : BaseActivity(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_lista_pagos)

        // Obtener el DNI pasado desde el intent
        val dni = intent.getStringExtra("DNI") ?: ""

        // Por ahora mostramos la lista fija (más adelante puedes filtrar por dni)
        val pagos = listOf(
            "Pago 1: $500 - Fecha: 15/05/2023",
        )

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPagos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ListAdapter(pagos, this)

        // Mostrar un Toast con el DNI recibido (para verificar)
        Toast.makeText(this, "DNI recibido: $dni", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(item: String) {
        Toast.makeText(this, "Seleccionaste: $item", Toast.LENGTH_SHORT).show()
    }
}

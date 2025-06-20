package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ExpirationsActivity : BaseActivity(), OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityLayout(R.layout.activity_expirations)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val textSinVencimientos = findViewById<TextView>(R.id.textSinVencimientos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHelper = UserDBHelper(this)
        val vencimientos = dbHelper.cuotasVencidas()

        if (vencimientos.isEmpty()) {
            textSinVencimientos.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textSinVencimientos.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            val adapter = ListAdapter(vencimientos, this)
            recyclerView.adapter = adapter
        }
    }



    override fun onItemClick(item: String, position: Int) {
        Toast.makeText(this, "Seleccionado: $item", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(item: String, position: Int) {
        // Por ahora no hacemos nada
    }
}

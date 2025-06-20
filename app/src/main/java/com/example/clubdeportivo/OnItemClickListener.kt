package com.example.clubdeportivo

interface OnItemClickListener {
    fun onItemClick(item: String, position: Int)
    fun onItemLongClick(item: String, position: Int)
}
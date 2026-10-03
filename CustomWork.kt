package com.example.workcost

data class CustomWork(
    val id: Long = System.currentTimeMillis(),
    var category: String,
    var name: String,
    var unit: String,
    var price: Double
)

data class CustomCategory(
    val id: Long = System.currentTimeMillis(),
    var name: String
)

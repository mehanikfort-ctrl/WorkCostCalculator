package com.example.workcost

data class Estimate(
    val id: Long = System.currentTimeMillis(),
    var name: String,
    var customer: String,
    var address: String,
    var date: String,
    var items: MutableList<EstimateItem> = mutableListOf()
) {
    fun totalSum(): Double = items.sumOf { it.sum() }
}

data class EstimateItem(
    val workId: Int,
    var name: String,
    var unit: String,
    var price: Double,
    var quantity: Double
) {
    fun sum(): Double = price * quantity
}

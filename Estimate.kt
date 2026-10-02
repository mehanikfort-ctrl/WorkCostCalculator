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

    // Превращаем смету в строку для сохранения
    fun toJsonString(): String {
        val itemsStr = items.joinToString("|") {
            "${it.workId};${it.name};${it.unit};${it.price};${it.quantity}"
        }
        return "$id\u0001$name\u0001$customer\u0001$address\u0001$date\u0001$itemsStr"
    }

    companion object {
        // Восстанавливаем смету из строки
        fun fromJsonString(str: String): Estimate? {
            return try {
                val parts = str.split("\u0001")
                if (parts.size < 6) return null
                val id = parts[0].toLongOrNull() ?: return null
                val name = parts[1]
                val customer = parts[2]
                val address = parts[3]
                val date = parts[4]
                val itemsStr = parts[5]
                val items = mutableListOf<EstimateItem>()
                if (itemsStr.isNotEmpty()) {
                    itemsStr.split("|").forEach { itemStr ->
                        val itemParts = itemStr.split(";")
                        if (itemParts.size == 5) {
                            items.add(
                                EstimateItem(
                                    workId = itemParts[0].toIntOrNull() ?: 0,
                                    name = itemParts[1],
                                    unit = itemParts[2],
                                    price = itemParts[3].toDoubleOrNull() ?: 0.0,
                                    quantity = itemParts[4].toDoubleOrNull() ?: 0.0
                                )
                            )
                        }
                    }
                }
                Estimate(id, name, customer, address, date, items)
            } catch (e: Exception) {
                null
            }
        }
    }
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

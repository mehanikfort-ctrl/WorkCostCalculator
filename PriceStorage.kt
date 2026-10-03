package com.example.workcost

import android.content.Context
import java.io.File

object PriceStorage {
    private const val FILE_NAME = "prices.txt"

    fun loadPrices(context: Context): Map<Int, Double> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyMap()
        val result = mutableMapOf<Int, Double>()
        file.readLines().forEach { line ->
            if (line.isNotBlank()) {
                val parts = line.split("=")
                if (parts.size == 2) {
                    val id = parts[0].toIntOrNull()
                    val price = parts[1].toDoubleOrNull()
                    if (id != null && price != null) {
                        result[id] = price
                    }
                }
            }
        }
        return result
    }

    fun savePrice(context: Context, workId: Int, price: Double) {
        val current = loadPrices(context).toMutableMap()
        current[workId] = price
        val content = current.entries.joinToString("\n") { "${it.key}=${it.value}" }
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(content)
    }

    fun resetAll(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) file.delete()
    }
}

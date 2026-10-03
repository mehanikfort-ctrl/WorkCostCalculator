package com.example.workcost

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EstimateImporter {
    fun importFromUri(context: Context, uri: Uri): Estimate? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()
            reader.close()

            var name = "Импортированная смета"
            var customer = ""
            var address = ""
            var date = ""
            val items = mutableListOf<EstimateItem>()
            var currentItemName: String? = null

            for (line in lines) {
                when {
                    line.startsWith("Объект: ") -> name = line.removePrefix("Объект: ").trim()
                    line.startsWith("Заказчик: ") -> customer = line.removePrefix("Заказчик: ").trim()
                    line.startsWith("Адрес: ") -> address = line.removePrefix("Адрес: ").trim()
                    line.startsWith("Дата: ") -> date = line.removePrefix("Дата: ").trim()
                    line.matches(Regex("^\\d+\\. .+")) -> {
                        currentItemName = line.replace(Regex("^\\d+\\. "), "").trim()
                    }
                    currentItemName != null && line.contains("×") && line.contains("=") -> {
                        val trimmed = line.trim()
                        val parts = trimmed.split("×")
                        if (parts.size >= 2) {
                            val qtyAndUnit = parts[0].trim().split(" ")
                            val qty = qtyAndUnit.getOrNull(0)?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                            val unit = qtyAndUnit.getOrNull(1) ?: "шт"

                            val pricePart = parts[1].trim().split("₽")[0].trim().replace(",", ".")
                            val price = pricePart.toDoubleOrNull() ?: 0.0

                            items.add(
                                EstimateItem(
                                    workId = (System.currentTimeMillis() + items.size).toInt(),
                                    name = currentItemName!!,
                                    unit = unit,
                                    price = price,
                                    quantity = qty
                                )
                            )
                            currentItemName = null
                        }
                    }
                }
            }

            if (items.isEmpty() && name == "Импортированная смета") return null

            Estimate(
                id = System.currentTimeMillis(),
                name = name,
                customer = customer,
                address = address,
                date = date.ifEmpty {
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                },
                items = items
            )
        } catch (e: Exception) {
            null
        }
    }
}

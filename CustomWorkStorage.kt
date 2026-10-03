package com.example.workcost

import android.content.Context
import java.io.File

object CustomWorkStorage {
    private const val WORKS_FILE = "custom_works.txt"
    private const val CATEGORIES_FILE = "custom_categories.txt"

    // === РАБОТЫ ===

    fun loadWorks(context: Context): MutableList<CustomWork> {
        val file = File(context.filesDir, WORKS_FILE)
        if (!file.exists()) return mutableListOf()
        val result = mutableListOf<CustomWork>()
        file.readLines().forEach { line ->
            if (line.isNotBlank()) {
                val parts = line.split("\u0001")
                if (parts.size == 5) {
                    val id = parts[0].toLongOrNull() ?: return@forEach
                    val category = parts[1]
                    val name = parts[2]
                    val unit = parts[3]
                    val price = parts[4].toDoubleOrNull() ?: 0.0
                    result.add(CustomWork(id, category, name, unit, price))
                }
            }
        }
        return result
    }

    fun saveAllWorks(context: Context, works: List<CustomWork>) {
        val content = works.joinToString("\n") {
            "${it.id}\u0001${it.category}\u0001${it.name}\u0001${it.unit}\u0001${it.price}"
        }
        File(context.filesDir, WORKS_FILE).writeText(content)
    }

    fun addWork(context: Context, work: CustomWork) {
        val existing = loadWorks(context)
        existing.add(work)
        saveAllWorks(context, existing)
    }

    fun deleteWork(context: Context, workId: Long) {
        val existing = loadWorks(context).filter { it.id != workId }
        saveAllWorks(context, existing)
    }

    // === РАЗДЕЛЫ ===

    fun loadCategories(context: Context): MutableList<CustomCategory> {
        val file = File(context.filesDir, CATEGORIES_FILE)
        if (!file.exists()) return mutableListOf()
        val result = mutableListOf<CustomCategory>()
        file.readLines().forEach { line ->
            if (line.isNotBlank()) {
                val parts = line.split("\u0001")
                if (parts.size == 2) {
                    val id = parts[0].toLongOrNull() ?: return@forEach
                    val name = parts[1]
                    result.add(CustomCategory(id, name))
                }
            }
        }
        return result
    }

    fun saveAllCategories(context: Context, categories: List<CustomCategory>) {
        val content = categories.joinToString("\n") { "${it.id}\u0001${it.name}" }
        File(context.filesDir, CATEGORIES_FILE).writeText(content)
    }

    fun addCategory(context: Context, category: CustomCategory) {
        val existing = loadCategories(context)
        existing.add(category)
        saveAllCategories(context, existing)
    }

    fun deleteCategory(context: Context, categoryName: String) {
        val existing = loadCategories(context).filter { it.name != categoryName }
        saveAllCategories(context, existing)
        // Также удаляем все работы из этого раздела
        val works = loadWorks(context).filter { it.category != categoryName }
        saveAllWorks(context, works)
    }
}

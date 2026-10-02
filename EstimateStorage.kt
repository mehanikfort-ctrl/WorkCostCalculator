package com.example.workcost

import android.content.Context
import java.io.File

object EstimateStorage {
    private const val FILE_NAME = "estimates.txt"

    fun saveAll(context: Context, estimates: List<Estimate>) {
        val file = File(context.filesDir, FILE_NAME)
        val content = estimates.joinToString("\n") { it.toJsonString() }
        file.writeText(content)
    }

    fun loadAll(context: Context): MutableList<Estimate> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return mutableListOf()
        val result = mutableListOf<Estimate>()
        file.readLines().forEach { line ->
            if (line.isNotBlank()) {
                Estimate.fromJsonString(line)?.let { result.add(it) }
            }
        }
        return result
    }

    fun addEstimate(context: Context, estimate: Estimate) {
        val existing = loadAll(context)
        existing.add(estimate)
        saveAll(context, existing)
    }

    fun deleteEstimate(context: Context, estimateId: Long) {
        val existing = loadAll(context).filter { it.id != estimateId }
        saveAll(context, existing)
    }
}

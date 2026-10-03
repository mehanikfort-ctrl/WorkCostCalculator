package com.example.workcost

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorksScreen(
    estimate: Estimate,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val categories = listOf("Строительные", "Земляные", "Электрика", "Кровля")
    var searchQuery by remember { mutableStateOf("") }
    var showEstimateDialog by remember { mutableStateOf(false) }

    // Сохраняем тексты ввода отдельно — это решает проблему с фокусом
    val volumeInputs = remember { mutableStateMapOf<Int, String>() }
    val priceInputs = remember { mutableStateMapOf<Int, String>() }

    fun saveCurrentEstimate() {
        val allEstimates = EstimateStorage.loadAll(context)
        val index = allEstimates.indexOfFirst { it.id == estimate.id }
        if (index >= 0) {
            allEstimates[index] = estimate
        } else {
            allEstimates.add(estimate)
        }
        EstimateStorage.saveAll(context, allEstimates)
    }

    fun exportEstimate() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy_HH-mm", Locale.getDefault())
        val fileName = "smeta_${estimate.name.replace(" ", "_")}_${dateFormat.format(Date())}.txt"

        val content = buildString {
            appendLine("=====================================")
            appendLine("СМЕТА НА ВЫПОЛНЕНИЕ РАБОТ")
            appendLine("=====================================")
            appendLine()
            appendLine("Объект: ${estimate.name}")
            appendLine("Заказчик: ${estimate.customer}")
            appendLine("Адрес: ${estimate.address}")
            appendLine("Дата: ${estimate.date}")
            appendLine()
            appendLine("-------------------------------------")
            appendLine("№  Наименование работ")
            appendLine("-------------------------------------")

            estimate.items.forEachIndexed { index, item ->
                appendLine("${index + 1}. ${item.name}")
                appendLine("   ${item.quantity} ${item.unit} × ${item.price} ₽ = ${"%.2f".format(item.sum())} ₽")
            }

            appendLine("-------------------------------------")
            appendLine("ИТОГО: ${"%.2f".format(estimate.totalSum())} ₽")
            appendLine("=====================================")
            appendLine()
            appendLine("Смета сформирована в приложении «Калькулятор смет»")
        }

        val file = File(context.cacheDir, fileName)
        file.writeText(content)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Смета: ${estimate.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Отправить смету"))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = {
                saveCurrentEstimate()
                onBack()
            }) { Text("← Назад") }
            Text(
                text = estimate.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск работ...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            categories.forEachIndexed { index, title ->
                Button(
                    onClick = { selectedTab = index },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = if (selectedTab == index) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text(title)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val currentCategory = categories[selectedTab]
        val allWorks = remember { WorkRepository.getWorks(context) }
        val filteredWorks = allWorks.filter {
            it.category == currentCategory &&
            (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredWorks, key = { it.id }) { work ->
                val existingItem = estimate.items.find { it.workId == work.id }
                val isSelected = existingItem != null

                // Инициализируем поля ввода при первом показе
                LaunchedEffect(work.id, isSelected) {
                    if (isSelected) {
                        volumeInputs.putIfAbsent(work.id, existingItem?.quantity?.toString() ?: "")
                        priceInputs.putIfAbsent(work.id, existingItem?.price?.toString() ?: work.price.toString())
                    } else {
                        volumeInputs.remove(work.id)
                        priceInputs.remove(work.id)
                    }
                }

                WorkRow(
                    work = work,
                    isSelected = isSelected,
                    volume = volumeInputs[work.id] ?: "",
                    price = priceInputs[work.id] ?: work.price.toString(),
                    onToggle = {
                        if (isSelected) {
                            estimate.items.remove(existingItem)
                        } else {
                            estimate.items.add(
                                EstimateItem(
                                    workId = work.id,
                                    name = work.name,
                                    unit = work.unit,
                                    price = work.price,
                                    quantity = 0.0
                                )
                            )
                        }
                        saveCurrentEstimate()
                    },
                    onVolumeChange = { newVolume ->
                        volumeInputs[work.id] = newVolume
                        existingItem?.quantity = newVolume.toDoubleOrNull() ?: 0.0
                        saveCurrentEstimate()
                    },
                    onPriceChange = { newPrice ->
                        priceInputs[work.id] = newPrice
                        existingItem?.price = newPrice.toDoubleOrNull() ?: work.price
                        saveCurrentEstimate()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showEstimateDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Смета")
            }
            Button(
                onClick = { exportEstimate() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Поделиться")
            }
        }
    }

    if (showEstimateDialog) {
        AlertDialog(
            onDismissRequest = { showEstimateDialog = false },
            title = { Text("Смета: ${estimate.name}") },
            text = {
                Column {
                    if (estimate.items.isEmpty()) {
                        Text("Ничего не выбрано")
                    } else {
                        estimate.items.forEach { item ->
                            Text("${item.name}: ${item.quantity} ${item.unit} × ${item.price} = ${"%.2f".format(item.sum())} ₽")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ИТОГО: ${"%.2f".format(estimate.totalSum())} ₽",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEstimateDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@Composable
fun WorkRow(
    work: WorkItem,
    isSelected: Boolean,
    volume: String,
    price: String,
    onToggle: () -> Unit,
    onVolumeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
                Column(modifier = Modifier.weight(1f)) {
                    Text(work.name, fontWeight = FontWeight.Bold)
                    Text("Ед. изм.: ${work.unit}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = volume,
                    onValueChange = onVolumeChange,
                    label = { Text("Объём") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = isSelected
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = onPriceChange,
                    label = { Text("Цена") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = isSelected
                )
            }
        }
    }
}

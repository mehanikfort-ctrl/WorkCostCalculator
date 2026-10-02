package com.example.workcost

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WorksScreen(
    estimate: Estimate,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val categories = listOf("Строительные", "Земляные", "Электрика", "Кровля")
    var searchQuery by remember { mutableStateOf("") }
    var showEstimateDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Верхняя панель с кнопкой "Назад" и названием сметы
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
            Text(
                text = estimate.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Строка поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск работ...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопки вкладок (категорий)
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

        // Список работ
        val currentCategory = categories[selectedTab]
        val filteredWorks = WorkRepository.allWorks.filter {
            it.category == currentCategory &&
            (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredWorks) { work ->
                val existingItem = estimate.items.find { it.workId == work.id }
                val isSelected = existingItem != null
                val volume = existingItem?.quantity?.toString() ?: ""
                val price = existingItem?.price?.toString() ?: work.price.toString()

                WorkRow(
                    work = work,
                    isSelected = isSelected,
                    volume = volume,
                    price = price,
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
                    },
                    onVolumeChange = { newVolume ->
                        existingItem?.quantity = newVolume.toDoubleOrNull() ?: 0.0
                    },
                    onPriceChange = { newPrice ->
                        existingItem?.price = newPrice.toDoubleOrNull() ?: work.price
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка "Сформировать смету"
        Button(
            onClick = { showEstimateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сформировать смету")
        }
    }

    // Диалог со сметой
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
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = onPriceChange,
                    label = { Text("Цена") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}

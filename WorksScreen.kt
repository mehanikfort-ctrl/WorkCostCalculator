package com.example.workcost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WorksScreen(
    estimate: Estimate,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val categories = remember { WorkRepository.getAllCategories(context) }
    val categoryIcons = listOf(
        Icons.Default.Build,
        Icons.Default.Landscape,
        Icons.Default.FlashOn,
        Icons.Default.Home
    )
    var searchQuery by remember { mutableStateOf("") }
    var showEstimateDialog by remember { mutableStateOf(false) }

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
                val icon = if (index < categoryIcons.size) categoryIcons[index] else Icons.Default.Build
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(title) },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val currentCategory = categories[selectedTab]
        val allWorks = remember { WorkRepository.getWorks(context) }
        val filteredWorks = allWorks
            .filter {
                it.category == currentCategory &&
                (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
            }
            .sortedByDescending { work ->
                estimate.items.any { it.workId == work.id }
            }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(filteredWorks, key = { _, work -> work.id }) { index, work ->
                val existingItem = estimate.items.find { it.workId == work.id }
                val isSelected = existingItem != null
                var isExpanded by remember { mutableStateOf(isSelected) }

                LaunchedEffect(work.id) {
                    if (isSelected) {
                        volumeInputs.putIfAbsent(work.id, existingItem?.quantity?.toString() ?: "")
                        priceInputs.putIfAbsent(work.id, existingItem?.price?.toString() ?: work.price.toString())
                    }
                }

                WorkRow(
                    number = index + 1,
                    work = work,
                    isSelected = isSelected,
                    isExpanded = isExpanded,
                    volume = volumeInputs[work.id] ?: "",
                    price = priceInputs[work.id] ?: work.price.toString(),
                    onExpandToggle = {
                        isExpanded = !isExpanded
                    },
                    onToggle = {
                        if (isSelected) {
                            estimate.items.remove(existingItem)
                            volumeInputs.remove(work.id)
                            priceInputs.remove(work.id)
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
                            volumeInputs[work.id] = ""
                            priceInputs[work.id] = work.price.toString()
                            isExpanded = true
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showEstimateDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.List, contentDescription = "Смета", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Смета")
            }
            Button(
                onClick = { PdfExporter.sharePdf(context, estimate) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF")
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
                        estimate.items.forEachIndexed { index, item ->
                            Text("${index + 1}. ${item.name}: ${item.quantity} ${item.unit} × ${item.price} = ${"%.2f".format(item.sum())} ₽")
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
    number: Int,
    work: WorkItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    volume: String,
    price: String,
    onExpandToggle: () -> Unit,
    onToggle: () -> Unit,
    onVolumeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onExpandToggle() }
            ) {
                Text(
                    text = "$number.",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(work.name, fontWeight = FontWeight.Bold)
                    Text("Ед. изм.: ${work.unit}", style = MaterialTheme.typography.bodySmall)
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
                        Text("Выбрать эту работу")
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
    }
}

package com.example.workcost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ManageWorksScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(0) }
    val categories = remember(refreshTrigger) { WorkRepository.getAllCategories(context) }
    val allWorks = remember(refreshTrigger) { WorkRepository.getWorks(context) }
    val customWorks = remember(refreshTrigger) { CustomWorkStorage.loadWorks(context) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
            Text(
                "Управление работами",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("+ Работа")
            }
            OutlinedButton(
                onClick = { showAddCategoryDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("+ Раздел")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Мои работы (${customWorks.size})", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (customWorks.isEmpty()) {
            Text("У вас пока нет своих работ. Добавьте первую!")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(customWorks, key = { it.id }) { work ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(work.name, fontWeight = FontWeight.Bold)
                                Text("${work.category} • ${work.unit} • ${work.price} ₽",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = {
                                CustomWorkStorage.deleteWork(context, work.id)
                                refreshTrigger++
                            }) {
                                Text("Удалить")
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог добавления работы
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("м2") }
        var price by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Новая работа") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Единица измерения (м2, м3, шт)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Цена") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Раздел:")
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceDouble = price.toDoubleOrNull()
                        if (name.isNotEmpty() && priceDouble != null && selectedCategory.isNotEmpty()) {
                            CustomWorkStorage.addWork(
                                context,
                                CustomWork(
                                    category = selectedCategory,
                                    name = name,
                                    unit = unit,
                                    price = priceDouble
                                )
                            )
                            refreshTrigger++
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог добавления раздела
    if (showAddCategoryDialog) {
        var name by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Новый раздел") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название раздела") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            CustomWorkStorage.addCategory(context, CustomCategory(name = name))
                            refreshTrigger++
                            showAddCategoryDialog = false
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

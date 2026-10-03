package com.example.workcost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(0) }
    val works = remember(refreshTrigger) { WorkRepository.getWorks(context) }
    var editingWorkId by remember { mutableStateOf<Int?>(null) }
    var newPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
            Text(
                text = "Настройки цен",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Нажмите на цену, чтобы изменить её.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(works, key = { it.id }) { work ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(work.name, fontWeight = FontWeight.Bold)
                            Text("${work.category} • ${work.unit}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (editingWorkId == work.id) {
                            OutlinedTextField(
                                value = newPrice,
                                onValueChange = { newPrice = it },
                                modifier = Modifier.width(120.dp),
                                singleLine = true,
                                label = { Text("Цена") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = {
                                val price = newPrice.toDoubleOrNull()
                                if (price != null) {
                                    PriceStorage.savePrice(context, work.id, price)
                                    refreshTrigger++
                                }
                                editingWorkId = null
                                newPrice = ""
                            }) {
                                Text("✓")
                            }
                        } else {
                            TextButton(onClick = {
                                editingWorkId = work.id
                                newPrice = work.price.toString()
                            }) {
                                Text("${work.price} ₽", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                PriceStorage.resetAll(context)
                refreshTrigger++
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить все цены к стандартным")
        }
    }
}

@Composable
fun IconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick) {
        content()
    }
}

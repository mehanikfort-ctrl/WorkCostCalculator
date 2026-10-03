package com.example.workcost

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EstimatesListScreen(
    onBack: () -> Unit,
    onOpenEstimate: (Estimate) -> Unit
) {
    val context = LocalContext.current
    var estimates by remember { mutableStateOf(EstimateStorage.loadAll(context)) }

    // Лончер для выбора файла
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val imported = EstimateImporter.importFromUri(context, uri)
            if (imported != null) {
                EstimateStorage.addEstimate(context, imported)
                estimates = EstimateStorage.loadAll(context)
                Toast.makeText(context, "Смета импортирована!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Не удалось прочитать файл", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
            Text(
                text = "Мои сметы",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { filePicker.launch(arrayOf("text/plain", "*/*")) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Upload, contentDescription = "Импорт", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Импорт")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (estimates.isEmpty()) {
            Text("Смет пока нет. Создайте первую смету в главном меню.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estimates, key = { it.id }) { estimate ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenEstimate(estimate) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(estimate.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Заказчик: ${estimate.customer}", style = MaterialTheme.typography.bodySmall)
                                Text("Адрес: ${estimate.address}", style = MaterialTheme.typography.bodySmall)
                                Text("Дата: ${estimate.date}", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("ИТОГО: ${"%.2f".format(estimate.totalSum())} ₽", fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = {
                                EstimateStorage.deleteEstimate(context, estimate.id)
                                estimates = EstimateStorage.loadAll(context)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}

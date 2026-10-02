package com.example.workcost

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun EstimatesListScreen(
    onBack: () -> Unit,
    onOpenEstimate: (Estimate) -> Unit
) {
    val context = LocalContext.current
    var estimates by remember { mutableStateOf(EstimateStorage.loadAll(context)) }

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
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(estimate.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Заказчик: ${estimate.customer}", style = MaterialTheme.typography.bodySmall)
                            Text("Адрес: ${estimate.address}", style = MaterialTheme.typography.bodySmall)
                            Text("Дата: ${estimate.date}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("ИТОГО: ${"%.2f".format(estimate.totalSum())} ₽", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

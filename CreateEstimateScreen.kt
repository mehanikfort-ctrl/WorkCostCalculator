package com.example.workcost

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateEstimateScreen(
    onBack: () -> Unit,
    onEstimateCreated: (Estimate) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    val date = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Новая смета", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название объекта") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = customer,
            onValueChange = { customer = it },
            label = { Text("Заказчик") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Адрес объекта") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = date,
            onValueChange = { },
            label = { Text("Дата") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Назад")
            }
            Button(
                onClick = {
                    val estimate = Estimate(
                        name = name.ifEmpty { "Без названия" },
                        customer = customer,
                        address = address,
                        date = date
                    )
                    EstimateStorage.addEstimate(context, estimate)
                    onEstimateCreated(estimate)
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotEmpty()
            ) {
                Text("Создать")
            }
        }
    }
}

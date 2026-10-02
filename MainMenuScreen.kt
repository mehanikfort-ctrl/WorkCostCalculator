package com.example.workcost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onCreateEstimate: () -> Unit,
    onShowEstimates: () -> Unit,
    onShowSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "КАЛЬКУЛЯТОР СМЕТ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        MenuCard(
            title = "➕ НОВАЯ СМЕТА",
            description = "Создать новую смету для объекта",
            onClick = onCreateEstimate
        )
        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(
            title = "📋 МОИ СМЕТЫ",
            description = "Просмотр сохранённых смет",
            onClick = onShowEstimates
        )
        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(
            title = "⚙️ НАСТРОЙКИ",
            description = "Цены, разделы, работы",
            onClick = onShowSettings
        )
    }
}

@Composable
fun MenuCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

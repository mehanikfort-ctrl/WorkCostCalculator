package com.example.workcost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
            icon = Icons.Default.Add,
            title = "НОВАЯ СМЕТА",
            description = "Создать новую смету для объекта",
            onClick = onCreateEstimate
        )
        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(
            icon = Icons.Default.List,
            title = "МОИ СМЕТЫ",
            description = "Просмотр сохранённых смет",
            onClick = onShowEstimates
        )
        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(
            icon = Icons.Default.Settings,
            title = "НАСТРОЙКИ",
            description = "Цены, разделы, работы",
            onClick = onShowSettings
        )
    }
}

@Composable
fun MenuCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

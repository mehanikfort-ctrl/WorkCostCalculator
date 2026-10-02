package com.example.workcost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("menu") }
    var currentEstimate by remember { mutableStateOf<Estimate?>(null) }

    when (currentScreen) {
        "menu" -> MainMenuScreen(
            onCreateEstimate = { currentScreen = "create" },
            onShowEstimates = { currentScreen = "estimates" },
            onShowSettings = { currentScreen = "settings" }
        )

        "create" -> CreateEstimateScreen(
            onBack = { currentScreen = "menu" },
            onEstimateCreated = { estimate ->
                currentEstimate = estimate
                currentScreen = "works"
            }
        )

        "works" -> currentEstimate?.let { estimate ->
            WorksScreenWrapper(
                estimate = estimate,
                onBack = { currentScreen = "menu" }
            )
        }

        "estimates" -> PlaceholderScreen("Мои сметы", onBack = { currentScreen = "menu" })
        "settings" -> PlaceholderScreen("Настройки", onBack = { currentScreen = "menu" })
    }
}

@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Этот раздел находится в разработке.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Назад в меню")
        }
    }
}

@Composable
fun WorksScreenWrapper(estimate: Estimate, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
            Text(
                text = estimate.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Здесь будет выбор работ для сметы.")
        Text("Название: ${estimate.name}")
        Text("Заказчик: ${estimate.customer}")
        Text("Адрес: ${estimate.address}")
        Text("Дата: ${estimate.date}")
    }
}

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
            WorksScreen(
                estimate = estimate,
                onBack = { currentScreen = "menu" }
            )
        }

        "estimates" -> EstimatesListScreen(
            onBack = { currentScreen = "menu" },
            onOpenEstimate = { estimate ->
                currentEstimate = estimate
                currentScreen = "works"
            }
        )

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

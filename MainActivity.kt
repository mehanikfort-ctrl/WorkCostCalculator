package com.example.workcost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Включаем полноэкранный режим
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        setContent {
            WorkCostTheme {
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

        "settings" -> SettingsScreen(
            onBack = { currentScreen = "menu" },
            onManageWorks = { currentScreen = "manage" }
        )

        "manage" -> ManageWorksScreen(
            onBack = { currentScreen = "settings" }
        )
    }
}

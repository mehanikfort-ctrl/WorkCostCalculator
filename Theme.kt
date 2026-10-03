package com.example.workcost

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// === СВЕТЛАЯ ТЕМА (строительная, тёплые тона) ===
private val LightColors = lightColorScheme(
    primary = Color(0xFFFF8F00),          // Янтарный (основной)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF3E2723),

    secondary = Color(0xFF5D4037),        // Коричневый
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7CCC8),
    onSecondaryContainer = Color(0xFF3E2723),

    tertiary = Color(0xFFEF6C00),         // Тёмно-оранжевый
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFFFFF8E1),       // Тёплый кремовый
    onBackground = Color(0xFF3E2723),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF3E2723),
    surfaceVariant = Color(0xFFFFECB3),
    onSurfaceVariant = Color(0xFF5D4037),

    outline = Color(0xFFBCAAA4),
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

// === ТЁМНАЯ ТЕМА ===
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB74D),          // Светло-янтарный
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFFE65100),
    onPrimaryContainer = Color(0xFFFFE0B2),

    secondary = Color(0xFFBCAAA4),
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF5D4037),
    onSecondaryContainer = Color(0xFFD7CCC8),

    tertiary = Color(0xFFFFCC80),
    onTertiary = Color(0xFF3E2723),

    background = Color(0xFF1E1A17),       // Тёмно-коричневый
    onBackground = Color(0xFFFFE0B2),
    surface = Color(0xFF2C2620),
    onSurface = Color(0xFFFFE0B2),
    surfaceVariant = Color(0xFF4E342E),
    onSurfaceVariant = Color(0xFFD7CCC8),

    outline = Color(0xFF8D6E63),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000)
)

@Composable
fun WorkCostTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

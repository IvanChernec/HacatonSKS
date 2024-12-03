package com.example.hacaton.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Определение цветовой схемы для светлой темы
private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF003366),
    onPrimary = Color.White,
    secondary = Color(0xFF66B2FF),
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Color(0xFF002244),
    surface = Color.White,
    onSurface = Color(0xFF002244)
)

// Определение цветовой схемы для темной темы
private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFEAEAEA),
    secondary = Color(0xFF162447),
    onSecondary = Color(0xFFEAEAEA),
    background = Color(0xFF0F0F1A),
    onBackground = Color.White,
    surface = Color(0xFF1A1A2E),
    onSurface = Color(0xFFFFFFFF)
)

@Composable
fun HacatonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography, // Вы можете определить свою типографику здесь
        content = content
    )
}
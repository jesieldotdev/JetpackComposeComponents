package com.jesiel.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),
    secondary = Color(0xFFFFA726),
    tertiary = Color(0xFFFF8A65),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF57C00),
    secondary = Color(0xFFFB8C00),
    tertiary = Color(0xFFFF9800),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
)

@Composable
fun MyTodosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

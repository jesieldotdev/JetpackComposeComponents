package com.jesiel.myapplication.ui.theme

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
import com.jesiel.myapplication.viewmodel.AppFont

// Cores padrão LARANJA para quando as cores dinâmicas estiverem DESATIVADAS
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),      // Laranja Claro
    secondary = Color(0xFFFFA726),
    tertiary = Color(0xFFFF8A65),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF57C00),     // Laranja Vibrante
    secondary = Color(0xFFFB8C00),
    tertiary = Color(0xFFFF9800),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
)

@Composable
fun myTodosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    appFont: AppFont = AppFont.POPPINS,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Se dynamicColor for true e o Android for 12+, usa as cores do sistema (Material You)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Caso contrário, usa as cores LARANJA definidas acima
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(appFont),
        content = content
    )
}

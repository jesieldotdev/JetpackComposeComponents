package com.jesiel.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jesiel.myapplication.data.AppFont

// Esta é uma função regular que será usada por todas as plataformas.
// Ela usa fontes do sistema como fallback para garantir que o build passe.
@Composable
fun getAppTypography(appFont: AppFont): Typography {
    val fontFamily = when (appFont) {
        AppFont.SYSTEM -> FontFamily.Default
        AppFont.POPPINS -> FontFamily.SansSerif // Fallback seguro
        AppFont.MONOSPACE -> FontFamily.Monospace
        AppFont.SERIF -> FontFamily.Serif
    }

    return Typography(
        displayLarge = TextStyle(fontFamily = fontFamily),
        displayMedium = TextStyle(fontFamily = fontFamily),
        displaySmall = TextStyle(fontFamily = fontFamily),
        headlineLarge = TextStyle(fontFamily = fontFamily),
        headlineMedium = TextStyle(fontFamily = fontFamily),
        headlineSmall = TextStyle(fontFamily = fontFamily),
        titleLarge = TextStyle(fontFamily = fontFamily),
        titleMedium = TextStyle(fontFamily = fontFamily),
        titleSmall = TextStyle(fontFamily = fontFamily),
        bodyLarge = TextStyle(fontFamily = fontFamily),
        bodyMedium = TextStyle(fontFamily = fontFamily),
        bodySmall = TextStyle(fontFamily = fontFamily),
        labelLarge = TextStyle(fontFamily = fontFamily),
        labelMedium = TextStyle(fontFamily = fontFamily),
        labelSmall = TextStyle(fontFamily = fontFamily)
    )
}

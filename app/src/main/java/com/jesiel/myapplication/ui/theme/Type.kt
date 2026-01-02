package com.jesiel.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.R
import com.jesiel.myapplication.viewmodel.AppFont

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
)

fun getTypography(appFont: AppFont): Typography {
    val fontFamily = when (appFont) {
        AppFont.SYSTEM -> FontFamily.Default
        AppFont.POPPINS -> Poppins
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

package com.jesiel.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = Poppins),
    displayMedium = TextStyle(fontFamily = Poppins),
    displaySmall = TextStyle(fontFamily = Poppins),
    headlineLarge = TextStyle(fontFamily = Poppins),
    headlineMedium = TextStyle(fontFamily = Poppins),
    headlineSmall = TextStyle(fontFamily = Poppins),
    titleLarge = TextStyle(fontFamily = Poppins),
    titleMedium = TextStyle(fontFamily = Poppins),
    titleSmall = TextStyle(fontFamily = Poppins),
    bodyLarge = TextStyle(fontFamily = Poppins),
    bodyMedium = TextStyle(fontFamily = Poppins),
    bodySmall = TextStyle(fontFamily = Poppins),
    labelLarge = TextStyle(fontFamily = Poppins),
    labelMedium = TextStyle(fontFamily = Poppins),
    labelSmall = TextStyle(fontFamily = Poppins)
)

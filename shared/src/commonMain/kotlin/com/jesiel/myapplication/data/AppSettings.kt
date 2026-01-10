package com.jesiel.myapplication.data

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val blurIntensity: Float = 20f,
    val backgroundImageUrl: String = "",
    val showBackgroundImage: Boolean = true,
    val selectedTaskCategory: String = "Tudo",
    val lastImageUpdateDay: Long = 0L,
    val themeName: String = "SYSTEM", // Usando String para evitar erros de compilação IR
    val useDynamicColors: Boolean = true,
    val isKanbanMode: Boolean = false,
    val isUserPro: Boolean = false,
    val fontName: String = "POPPINS" // Usando String
)

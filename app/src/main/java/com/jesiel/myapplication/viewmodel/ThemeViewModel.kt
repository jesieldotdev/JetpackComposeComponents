package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

data class ThemeState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val useDynamicColors: Boolean = true
)

class ThemeViewModel : ViewModel() {
    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _themeState.update { it.copy(theme = theme) }
    }

    fun setDynamicColors(enabled: Boolean) {
        _themeState.update { it.copy(useDynamicColors = enabled) }
    }
}

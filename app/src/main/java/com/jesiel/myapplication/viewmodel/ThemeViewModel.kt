package com.jesiel.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.PreferenceManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

data class ThemeState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val useDynamicColors: Boolean = true
)

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val preferenceManager = PreferenceManager(application)

    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    init {
        // Load saved preferences
        viewModelScope.launch {
            combine(preferenceManager.theme, preferenceManager.useDynamicColors) { theme, dynamic ->
                ThemeState(theme, dynamic)
            }.collect { newState ->
                _themeState.value = newState
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferenceManager.setTheme(theme)
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setDynamicColors(enabled)
        }
    }
}

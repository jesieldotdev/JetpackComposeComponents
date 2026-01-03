package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ThemeState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val useDynamicColors: Boolean = true,
    val isKanbanMode: Boolean = false,
    val isUserPro: Boolean = false,
    val font: AppFont = AppFont.POPPINS
)

class ThemeViewModel(private val preferenceManager: IPreferenceManager) : ViewModel() {

    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferenceManager.theme,
                preferenceManager.useDynamicColors,
                preferenceManager.isKanbanMode,
                preferenceManager.isUserPro,
                preferenceManager.font
            ) { theme, dynamicColors, kanbanMode, isPro, font ->
                ThemeState(theme, dynamicColors, kanbanMode, isPro, font)
            }.collect {
                _themeState.value = it
            }
        }
    }

    fun setTheme(theme: AppTheme) { viewModelScope.launch { preferenceManager.setTheme(theme) } }
    fun setDynamicColors(enabled: Boolean) { viewModelScope.launch { preferenceManager.setDynamicColors(enabled) } }
    fun setKanbanMode(enabled: Boolean) { viewModelScope.launch { preferenceManager.setKanbanMode(enabled) } }
    fun setUserPro(enabled: Boolean) { viewModelScope.launch { preferenceManager.setUserPro(enabled) } }
    fun setFont(font: AppFont) { viewModelScope.launch { preferenceManager.setFont(font) } }
}

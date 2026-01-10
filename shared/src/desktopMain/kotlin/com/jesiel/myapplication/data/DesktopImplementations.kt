package com.jesiel.myapplication.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Properties

class DesktopPreferenceManager : IPreferenceManager {
    private val prefsFile = File(System.getProperty("user.home"), ".taska_settings.properties")
    private val properties = Properties()
    
    private val _state = MutableStateFlow(loadSettings())
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        if (prefsFile.exists()) {
            try {
                prefsFile.inputStream().use { properties.load(it) }
                _state.value = loadSettings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadSettings(): AppSettings {
        return AppSettings(
            blurIntensity = properties.getProperty("blurIntensity", "20.0").toFloat(),
            backgroundImageUrl = properties.getProperty("backgroundImageUrl", ""),
            showBackgroundImage = properties.getProperty("showBackgroundImage", "true").toBoolean(),
            selectedTaskCategory = properties.getProperty("selectedTaskCategory", "Tudo"),
            lastImageUpdateDay = properties.getProperty("lastImageUpdateDay", "0").toLong(),
            themeName = properties.getProperty("themeName", "SYSTEM"),
            useDynamicColors = properties.getProperty("useDynamicColors", "true").toBoolean(),
            isKanbanMode = properties.getProperty("isKanbanMode", "false").toBoolean(),
            isUserPro = properties.getProperty("isUserPro", "false").toBoolean(),
            fontName = properties.getProperty("fontName", "POPPINS")
        )
    }

    private fun save() {
        try {
            val s = _state.value
            properties.setProperty("blurIntensity", s.blurIntensity.toString())
            properties.setProperty("backgroundImageUrl", s.backgroundImageUrl)
            properties.setProperty("showBackgroundImage", s.showBackgroundImage.toString())
            properties.setProperty("selectedTaskCategory", s.selectedTaskCategory)
            properties.setProperty("lastImageUpdateDay", s.lastImageUpdateDay.toString())
            properties.setProperty("themeName", s.themeName)
            properties.setProperty("useDynamicColors", s.useDynamicColors.toString())
            properties.setProperty("isKanbanMode", s.isKanbanMode.toString())
            properties.setProperty("isUserPro", s.isUserPro.toString())
            properties.setProperty("fontName", s.fontName)
            
            prefsFile.outputStream().use { properties.store(it, "Taska App Settings") }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val blurIntensity: Flow<Float> = _state.map { it.blurIntensity }
    override val backgroundImageUrl: Flow<String> = _state.map { it.backgroundImageUrl }
    override val showBackgroundImage: Flow<Boolean> = _state.map { it.showBackgroundImage }
    override val selectedTaskCategory: Flow<String> = _state.map { it.selectedTaskCategory }
    override val lastImageUpdateDay: Flow<Long> = _state.map { it.lastImageUpdateDay }
    
    override val theme: Flow<AppTheme> = _state.map { 
        try { AppTheme.valueOf(it.themeName) } catch(e: Exception) { AppTheme.SYSTEM }
    }
    
    override val useDynamicColors: Flow<Boolean> = _state.map { it.useDynamicColors }
    override val isKanbanMode: Flow<Boolean> = _state.map { it.isKanbanMode }
    override val isUserPro: Flow<Boolean> = _state.map { it.isUserPro }
    
    override val font: Flow<AppFont> = _state.map { 
        try { AppFont.valueOf(it.fontName) } catch(e: Exception) { AppFont.POPPINS }
    }

    override suspend fun setBlurIntensity(intensity: Float) { _state.update { it.copy(blurIntensity = intensity) }; save() }
    override suspend fun setBackgroundImage(url: String, day: Long) { _state.update { it.copy(backgroundImageUrl = url, lastImageUpdateDay = day) }; save() }
    override suspend fun setShowBackgroundImage(enabled: Boolean) { _state.update { it.copy(showBackgroundImage = enabled) }; save() }
    override suspend fun setSelectedTaskCategory(category: String) { _state.update { it.copy(selectedTaskCategory = category) }; save() }
    override suspend fun setTheme(theme: AppTheme) { _state.update { it.copy(themeName = theme.name) }; save() }
    override suspend fun setDynamicColors(enabled: Boolean) { _state.update { it.copy(useDynamicColors = enabled) }; save() }
    override suspend fun setKanbanMode(enabled: Boolean) { _state.update { it.copy(isKanbanMode = enabled) }; save() }
    override suspend fun setUserPro(enabled: Boolean) { _state.update { it.copy(isUserPro = enabled) }; save() }
    override suspend fun setFont(font: AppFont) { _state.update { it.copy(fontName = font.name) }; save() }
}

class DesktopReminderManager : IReminderManager {
    override fun scheduleReminder(timeInMillis: Long, title: String, description: String?, id: Int) {}
    override fun cancelReminder(id: Int) {}
}

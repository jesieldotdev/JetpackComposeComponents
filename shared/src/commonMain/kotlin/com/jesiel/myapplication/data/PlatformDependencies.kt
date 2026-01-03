package com.jesiel.myapplication.data

import kotlinx.coroutines.flow.Flow

enum class AppTheme { LIGHT, DARK, SYSTEM }
enum class AppFont { SYSTEM, POPPINS, MONOSPACE, SERIF }

interface IPreferenceManager {
    val blurIntensity: Flow<Float>
    val backgroundImageUrl: Flow<String>
    val showBackgroundImage: Flow<Boolean>
    val selectedTaskCategory: Flow<String>
    val lastImageUpdateDay: Flow<Long>
    
    // Novas propriedades de tema
    val theme: Flow<AppTheme>
    val useDynamicColors: Flow<Boolean>
    val isKanbanMode: Flow<Boolean>
    val isUserPro: Flow<Boolean>
    val font: Flow<AppFont>

    suspend fun setBlurIntensity(intensity: Float)
    suspend fun setBackgroundImage(url: String, day: Long)
    suspend fun setShowBackgroundImage(enabled: Boolean)
    suspend fun setSelectedTaskCategory(category: String)
    
    suspend fun setTheme(theme: AppTheme)
    suspend fun setDynamicColors(enabled: Boolean)
    suspend fun setKanbanMode(enabled: Boolean)
    suspend fun setUserPro(enabled: Boolean)
    suspend fun setFont(font: AppFont)
}

interface IReminderManager {
    fun scheduleReminder(timeInMillis: Long, title: String, description: String?, id: Int)
    fun cancelReminder(id: Int)
}

package com.jesiel.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.jesiel.myapplication.viewmodel.AppFont
import com.jesiel.myapplication.viewmodel.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("app_theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val BLUR_INTENSITY = floatPreferencesKey("blur_intensity")
        val BACKGROUND_IMAGE = stringPreferencesKey("background_image")
        val LAST_IMAGE_UPDATE_DAY = longPreferencesKey("last_image_update_day")
        val KANBAN_MODE = booleanPreferencesKey("kanban_mode")
        val IS_USER_PRO = booleanPreferencesKey("is_user_pro")
        val SHOW_BACKGROUND_IMAGE = booleanPreferencesKey("show_background_image")
        val SELECTED_TASK_CATEGORY = stringPreferencesKey("selected_task_category")
        val SELECTED_HABIT_CATEGORY = stringPreferencesKey("selected_habit_category")
        val APP_FONT = stringPreferencesKey("app_font")
    }

    val theme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val themeName = preferences[Keys.THEME] ?: AppTheme.SYSTEM.name
        AppTheme.valueOf(themeName)
    }

    val useDynamicColors: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.DYNAMIC_COLORS] ?: true
    }

    val blurIntensity: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[Keys.BLUR_INTENSITY] ?: 20f
    }

    val backgroundImageUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.BACKGROUND_IMAGE] ?: ""
    }

    val lastImageUpdateDay: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[Keys.LAST_IMAGE_UPDATE_DAY] ?: 0L
    }

    val isKanbanMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.KANBAN_MODE] ?: false
    }

    val isUserPro: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.IS_USER_PRO] ?: false
    }

    val showBackgroundImage: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.SHOW_BACKGROUND_IMAGE] ?: true
    }

    val selectedTaskCategory: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.SELECTED_TASK_CATEGORY] ?: "Tudo"
    }

    val selectedHabitCategory: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.SELECTED_HABIT_CATEGORY] ?: "Tudo"
    }

    val font: Flow<AppFont> = context.dataStore.data.map { preferences ->
        val fontName = preferences[Keys.APP_FONT] ?: AppFont.POPPINS.name
        AppFont.valueOf(fontName)
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun setBlurIntensity(intensity: Float) {
        context.dataStore.edit { preferences ->
            preferences[Keys.BLUR_INTENSITY] = intensity
        }
    }

    suspend fun setBackgroundImage(url: String, dayEpoch: Long) {
        context.dataStore.edit { preferences ->
            preferences[Keys.BACKGROUND_IMAGE] = url
            preferences[Keys.LAST_IMAGE_UPDATE_DAY] = dayEpoch
        }
    }

    suspend fun setKanbanMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.KANBAN_MODE] = enabled
        }
    }

    suspend fun setUserPro(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_USER_PRO] = enabled
        }
    }

    suspend fun setShowBackgroundImage(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SHOW_BACKGROUND_IMAGE] = enabled
        }
    }

    suspend fun setSelectedTaskCategory(category: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SELECTED_TASK_CATEGORY] = category
        }
    }

    suspend fun setSelectedHabitCategory(category: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SELECTED_HABIT_CATEGORY] = category
        }
    }

    suspend fun setFont(font: AppFont) {
        context.dataStore.edit { preferences ->
            preferences[Keys.APP_FONT] = font.name
        }
    }
}

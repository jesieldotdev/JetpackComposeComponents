package com.jesiel.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
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
}

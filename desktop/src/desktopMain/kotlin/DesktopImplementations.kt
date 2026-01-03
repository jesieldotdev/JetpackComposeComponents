import com.jesiel.myapplication.data.IPreferenceManager
import com.jesiel.myapplication.data.IReminderManager
import com.jesiel.myapplication.data.AppTheme
import com.jesiel.myapplication.data.AppFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DesktopPreferenceManager : IPreferenceManager {
    // Usamos MutableStateFlow para que os dados possam ser alterados e observados
    private val _blurIntensity = MutableStateFlow(20f)
    private val _backgroundImageUrl = MutableStateFlow("")
    private val _showBackgroundImage = MutableStateFlow(true)
    private val _selectedTaskCategory = MutableStateFlow("Tudo")
    private val _lastImageUpdateDay = MutableStateFlow(0L)
    private val _theme = MutableStateFlow(AppTheme.SYSTEM)
    private val _useDynamicColors = MutableStateFlow(true)
    private val _isKanbanMode = MutableStateFlow(false)
    private val _isUserPro = MutableStateFlow(false)
    private val _font = MutableStateFlow(AppFont.POPPINS)

    override val blurIntensity: Flow<Float> = _blurIntensity.asStateFlow()
    override val backgroundImageUrl: Flow<String> = _backgroundImageUrl.asStateFlow()
    override val showBackgroundImage: Flow<Boolean> = _showBackgroundImage.asStateFlow()
    override val selectedTaskCategory: Flow<String> = _selectedTaskCategory.asStateFlow()
    override val lastImageUpdateDay: Flow<Long> = _lastImageUpdateDay.asStateFlow()
    override val theme: Flow<AppTheme> = _theme.asStateFlow()
    override val useDynamicColors: Flow<Boolean> = _useDynamicColors.asStateFlow()
    override val isKanbanMode: Flow<Boolean> = _isKanbanMode.asStateFlow()
    override val isUserPro: Flow<Boolean> = _isUserPro.asStateFlow()
    override val font: Flow<AppFont> = _font.asStateFlow()

    // Implementação real dos métodos de atualização
    override suspend fun setBlurIntensity(intensity: Float) { _blurIntensity.value = intensity }
    override suspend fun setBackgroundImage(url: String, day: Long) { 
        _backgroundImageUrl.value = url
        _lastImageUpdateDay.value = day
    }
    override suspend fun setShowBackgroundImage(enabled: Boolean) { _showBackgroundImage.value = enabled }
    override suspend fun setSelectedTaskCategory(category: String) { _selectedTaskCategory.value = category }
    
    override suspend fun setTheme(theme: AppTheme) { _theme.value = theme }
    override suspend fun setDynamicColors(enabled: Boolean) { _useDynamicColors.value = enabled }
    override suspend fun setKanbanMode(enabled: Boolean) { _isKanbanMode.value = enabled }
    override suspend fun setUserPro(enabled: Boolean) { _isUserPro.value = enabled }
    override suspend fun setFont(font: AppFont) { _font.value = font }
}

class DesktopReminderManager : IReminderManager {
    override fun scheduleReminder(timeInMillis: Long, title: String, description: String?, id: Int) {
        println("Lembrete agendado no Desktop: $title")
    }
    override fun cancelReminder(id: Int) {}
}

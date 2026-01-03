import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.*
import androidx.compose.foundation.isSystemInDarkTheme
import com.jesiel.myapplication.ui.theme.MyTodosTheme
import com.jesiel.myapplication.ui.screens.HomeScreen
import com.jesiel.myapplication.ui.screens.SettingsScreen
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.viewmodel.ThemeViewModel
import com.jesiel.myapplication.data.AppTheme

enum class Screen { Home, Settings }

fun main() = application {
    val preferenceManager = remember { DesktopPreferenceManager() }
    val reminderManager = remember { DesktopReminderManager() }
    
    val todoViewModel = remember { TodoViewModel(preferenceManager, reminderManager) }
    val themeViewModel = remember { ThemeViewModel(preferenceManager) }

    val themeState by themeViewModel.themeState.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    // Lógica para decidir se usa Dark Theme
    val useDarkTheme = when (themeState.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Taska App - Desktop",
    ) {
        MyTodosTheme(darkTheme = useDarkTheme) { // Passando o estado reativo
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    todoViewModel = todoViewModel,
                    onNavigateToSettings = { currentScreen = Screen.Settings }
                )
                Screen.Settings -> SettingsScreen(
                    todoViewModel = todoViewModel,
                    themeViewModel = themeViewModel,
                    onNavigateBack = { currentScreen = Screen.Home }
                )
            }
        }
    }
}

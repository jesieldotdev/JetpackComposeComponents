import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import com.jesiel.myapplication.ui.theme.MyTodosTheme
import com.jesiel.myapplication.ui.screens.*
import com.jesiel.myapplication.viewmodel.*
import com.jesiel.myapplication.data.AppTheme
import com.jesiel.myapplication.data.DesktopPreferenceManager
import com.jesiel.myapplication.data.DesktopReminderManager

sealed class Screen {
    object Home : Screen()
    object Settings : Screen()
    object Habits : Screen()
    data class Detail(val taskId: Int) : Screen()
}

@Composable
fun TaskaApp() {
    val preferenceManager = remember { DesktopPreferenceManager() }
    val reminderManager = remember { DesktopReminderManager() }
    
    val todoViewModel = remember { TodoViewModel(preferenceManager, reminderManager) }
    val themeViewModel = remember { ThemeViewModel(preferenceManager) }
    val habitViewModel = remember { HabitViewModel() }

    val themeState by themeViewModel.themeState.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val systemInDark = isSystemInDarkTheme()
    val useDarkTheme = when (themeState.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> systemInDark
    }

    MyTodosTheme(
        darkTheme = useDarkTheme,
        appFont = themeState.font
    ) { 
        when (val screen = currentScreen) {
            is Screen.Home -> HomeScreen(
                todoViewModel = todoViewModel,
                habitViewModel = habitViewModel, // CORREÇÃO: Parâmetro adicionado
                isKanbanMode = themeState.isKanbanMode,
                onNavigateToSettings = { currentScreen = Screen.Settings },
                onNavigateToHabits = { currentScreen = Screen.Habits },
                onNavigateToDetail = { taskId -> currentScreen = Screen.Detail(taskId) }
            )
            is Screen.Settings -> SettingsScreen(
                todoViewModel = todoViewModel,
                themeViewModel = themeViewModel,
                onNavigateBack = { currentScreen = Screen.Home }
            )
            is Screen.Habits -> HabitScreen(
                habitViewModel = habitViewModel,
                todoViewModel = todoViewModel,
                onNavigateToHome = { currentScreen = Screen.Home },
                onNavigateToSettings = { currentScreen = Screen.Settings }
            )
            is Screen.Detail -> TaskDetailScreen(
                taskId = screen.taskId,
                todoViewModel = todoViewModel,
                onNavigateBack = { currentScreen = Screen.Home }
            )
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Taska App - Desktop",
        state = rememberWindowState(width = 1280.dp, height = 720.dp)
    ) {
        TaskaApp()
    }
}

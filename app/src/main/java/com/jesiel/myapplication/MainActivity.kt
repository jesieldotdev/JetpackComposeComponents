package com.jesiel.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jesiel.myapplication.ui.screens.*
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.*

class MainActivity : ComponentActivity() {

    private var currentIntent by mutableStateOf<Intent?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()
        currentIntent = intent

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeState by themeViewModel.themeState.collectAsState()
            
            val useDarkTheme = when (themeState.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            myTodosTheme(
                darkTheme = useDarkTheme,
                dynamicColor = themeState.useDynamicColors,
                appFont = themeState.font
            ) {
                AppNavigation(themeViewModel = themeViewModel, intent = currentIntent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent = intent
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel, intent: Intent?) {
    val navController = rememberNavController()
    val todoViewModel: TodoViewModel = viewModel()
    val habitViewModel: HabitViewModel = viewModel()
    val uiState by todoViewModel.uiState.collectAsState()

    // Handle deep link navigation from notifications
    LaunchedEffect(intent) {
        intent?.let {
            val taskId = it.getIntExtra("navigate_to_task_id", -1)
            if (taskId != -1) {
                it.removeExtra("navigate_to_task_id")
                navController.navigate("detail/$taskId") {
                    popUpTo("home")
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("login") { LoginScreen(navController) }
        
        composable("home") { 
            HomeScreen(
                todoViewModel = todoViewModel,
                themeViewModel = themeViewModel,
                onNavigateToDetail = { taskId -> 
                    navController.navigate("detail/$taskId") 
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToHabits = {
                    navController.navigate("habits")
                }
            ) 
        }
        
        composable(
            route = "detail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            TaskDetailScreen(
                taskId = taskId,
                todoViewModel = todoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("about") {
            AboutScreen(
                backgroundImageUrl = uiState.backgroundImageUrl,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                todoViewModel = todoViewModel,
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("habits") {
            HabitScreen(
                habitViewModel = habitViewModel,
                todoViewModel = todoViewModel,
                themeViewModel = themeViewModel,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate("habit_detail/$habitId")
                }
            )
        }

        composable(
            route = "habit_detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: -1
            HabitDetailScreen(
                habitId = habitId,
                habitViewModel = habitViewModel,
                todoViewModel = todoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

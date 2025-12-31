package com.jesiel.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jesiel.myapplication.ui.screens.AboutScreen
import com.jesiel.myapplication.ui.screens.HomeScreen
import com.jesiel.myapplication.ui.screens.LoginScreen
import com.jesiel.myapplication.ui.screens.SettingsScreen
import com.jesiel.myapplication.ui.screens.TaskDetailScreen
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.AppTheme
import com.jesiel.myapplication.viewmodel.ThemeViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {

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
                dynamicColor = themeState.useDynamicColors
            ) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
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
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val todoViewModel: TodoViewModel = viewModel()
    val uiState by todoViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("login") { LoginScreen(navController) }
        
        composable("home") { 
            HomeScreen(
                todoViewModel = todoViewModel,
                onNavigateToDetail = { taskId -> 
                    navController.navigate("detail/$taskId") 
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
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
    }
}

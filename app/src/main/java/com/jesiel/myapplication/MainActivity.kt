package com.jesiel.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jesiel.myapplication.ui.screens.HomeScreen
import com.jesiel.myapplication.ui.screens.LoginScreen
import com.jesiel.myapplication.ui.screens.TaskDetailScreen
import com.jesiel.myapplication.ui.theme.myTodosTheme
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
            myTodosTheme(dynamicColor = true) {
                AppNavigation()
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
fun AppNavigation() {
    val navController = rememberNavController()
    // Sharing the ViewModel across screens
    val todoViewModel: TodoViewModel = viewModel()

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
    }
}

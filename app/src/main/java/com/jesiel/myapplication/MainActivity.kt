package com.jesiel.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jesiel.myapplication.ui.screens.HomeScreen
import com.jesiel.myapplication.ui.screens.LoginScreen
import com.jesiel.myapplication.ui.theme.myTodosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            myTodosTheme(
                dynamicColor = true // Enabled Material You
            ) {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen() } // Corrected: HomeScreen now manages its own state
    }
}

package com.jesiel.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                var showSheet by remember { mutableStateOf(false) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showSheet = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add"
                            )
                        }
                    },
                ) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        showSheet = showSheet,
                        onDismissSheet = { showSheet = false }

                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    showSheet: Boolean,
    onDismissSheet: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(
            showSheet = showSheet,
            onDismissSheet = onDismissSheet
        ) }
    }
}

package com.jesiel.myapplication.ui.components.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    title: String = "Detalhes da Tarefa",
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, 
                    contentDescription = "Voltar", 
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

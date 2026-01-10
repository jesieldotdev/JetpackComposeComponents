package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.detail.DetailTopBar
import com.jesiel.myapplication.ui.components.detail.TaskDetailCard
import com.jesiel.myapplication.viewmodel.TodoViewModel

@Composable
fun TaskDetailScreen(
    taskId: Int,
    todoViewModel: TodoViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by todoViewModel.uiState.collectAsState()
    val task = uiState.tasks.find { it.id == taskId }
    var showEditSheet by remember { mutableStateOf(false) }

    if (task == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showBackgroundImage) {
            BlurredBackground(
                imageUrl = uiState.backgroundImageUrl, 
                blurIntensity = uiState.blurIntensity,
                scrimAlpha = 0.6f
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { 
                DetailTopBar(
                    title = "Detalhes da Tarefa",
                    onNavigateBack = onNavigateBack
                ) 
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showEditSheet = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Tarefa")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {
                TaskDetailCard(task = task)
            }
        }

        // Nota: O ExampleBottomSheet deve ser movido para o shared para funcionar no Desktop
        // Por enquanto, deixamos comentado ou implementamos uma versão simples
    }
}

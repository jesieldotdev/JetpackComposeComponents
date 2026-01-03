package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.components.home.TaskListView
import com.jesiel.myapplication.ui.components.home.CategoryFilterBar
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.form.TaskInputField
import com.jesiel.myapplication.ui.components.form.ColorPicker
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.data.TaskStatus

@Composable
fun HomeScreen(
    todoViewModel: TodoViewModel,
    onNavigateToSettings: () -> Unit = {},
) {
    val uiState by todoViewModel.uiState.collectAsState()
    
    val (pendingTasks, doneTasks) = remember(uiState.tasks) {
        uiState.tasks.partition { it.status != TaskStatus.DONE }
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3")
    var selectedColor by remember { mutableStateOf(colors[0]) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showBackgroundImage) {
            BlurredBackground(
                imageUrl = uiState.backgroundImageUrl,
                blurIntensity = uiState.blurIntensity,
                scrimAlpha = 0.8f
            )
        }

        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            
            // 1. SIDEBAR
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(primaryColor, MaterialTheme.colorScheme.secondary)))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
                IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, null, tint = Color.White.copy(alpha = 0.7f)) }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ExitToApp, null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. CONTEÚDO PRINCIPAL
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(surfaceColor.copy(alpha = 0.95f))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Olá, Bob!", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Dashboard de Tarefas", color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    CategoryFilterBar(
                        categories = listOf("Tudo", "dev", "Compras", "Features"),
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = {}
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(0.6f)) {
                        Text("Minhas Tarefas", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        TaskListView(
                            pendingTasks = pendingTasks,
                            doneTasks = doneTasks,
                            onToggleTaskStatus = { todoViewModel.toggleTaskStatus(it) }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.4f).padding(start = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(progress = { 0.84f }, modifier = Modifier.fillMaxSize(), color = primaryColor, strokeWidth = 12.dp)
                            Text("84%", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = primaryColor)
                        }
                        Text("Progresso Diário", fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 3. PAINEL DE ADIÇÃO (Idêntico ao Mobile)
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(surfaceColor.copy(alpha = 0.95f))
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Nova Tarefa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                Spacer(modifier = Modifier.height(32.dp))
                
                TaskInputField(value = title, onValueChange = { title = it }, label = "O que fazer?", icon = Icons.Default.Create)
                Spacer(modifier = Modifier.height(20.dp))
                
                TaskInputField(value = description, onValueChange = { description = it }, label = "Detalhes", icon = Icons.Default.Edit, isMultiline = true)
                Spacer(modifier = Modifier.height(32.dp))
                
                TaskInputField(value = category, onValueChange = { category = it }, label = "Categoria", icon = Icons.Default.List)
                Spacer(modifier = Modifier.height(24.dp))
                
                ColorPicker(colors = colors, selectedColor = selectedColor, onColorSelected = { selectedColor = it })
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = { /* todoViewModel.addTodo(...) */ },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Criar Tarefa", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.components.home.TaskListView
import com.jesiel.myapplication.ui.components.home.CategoryFilterBar
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.form.TaskInputField
import com.jesiel.myapplication.ui.components.form.ColorPicker
import com.jesiel.myapplication.ui.components.Header
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.data.TaskStatus
import com.jesiel.myapplication.ui.components.DesktopSidebar

@Composable
fun HomeScreen(
    todoViewModel: TodoViewModel,
    habitViewModel: HabitViewModel, // Adicionado para o Header inteligente
    isKanbanMode: Boolean = false,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHabits: () -> Unit = {},
    onExit: () -> Unit,
    onNavigateToHome: () -> Unit = {},

) {
    val uiState by todoViewModel.uiState.collectAsState()
    
    val (pendingTasks, doneTasks) = remember(uiState.tasks, uiState.selectedCategory) {
        val filtered = if (uiState.selectedCategory == "Tudo") uiState.tasks else uiState.tasks.filter { it.category == uiState.selectedCategory }
        filtered.partition { it.status != TaskStatus.DONE }
    }

    val categories = remember(uiState.tasks) {
        listOf("Tudo") + uiState.tasks.mapNotNull { it.category }.filter { it.isNotBlank() }.distinct()
    }

    // Estados do Formulário Lateral
    var editingTaskId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3")
    var selectedColor by remember { mutableStateOf(colors[0]) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showBackgroundImage) {
            BlurredBackground(imageUrl = uiState.backgroundImageUrl, blurIntensity = uiState.blurIntensity, scrimAlpha = 0.8f)
        }

        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            
            // 1. SIDEBAR
            DesktopSidebar(
                onNavigateToHabits,
                onNavigateToSettings,
                onExit,
                onNavigateToHome
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. CONTEÚDO PRINCIPAL
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
//                    .background(surfaceColor.copy(alpha = 0.95f))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Header(
                        todoViewModel = todoViewModel,
                        habitViewModel = habitViewModel
                    )
                    CategoryFilterBar(
                        categories = categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { todoViewModel.setSelectedCategory(it) }
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(0.6f)) {
                        Text("Minhas Tarefas", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        
                        if (isKanbanMode) {
                            KanbanScreen(
                                uiState = uiState,
                                onUpdateStatus = { id, status -> 
                                    val t = uiState.tasks.find { it.id == id }
                                    todoViewModel.updateTask(id, t?.title ?: "", t?.description, t?.category, t?.color, t?.reminder) 
                                },
                                onDeleteTask = { todoViewModel.deleteTodo(it) },
                                onTaskClick = { id ->
                                    val task = uiState.tasks.find { it.id == id }
                                    if (task != null) {
                                        editingTaskId = task.id
                                        title = task.title
                                        description = task.description ?: ""
                                        category = task.category ?: ""
                                        selectedColor = task.color ?: colors[0]
                                    }
                                }
                            )
                        } else {
                            TaskListView(
                                pendingTasks = pendingTasks,
                                doneTasks = doneTasks,
                                onToggleTaskStatus = { todoViewModel.toggleTaskStatus(it) },
                                onDeleteTask = { todoViewModel.deleteTodo(it) },
                                onTaskClick = { id ->
                                    val task = uiState.tasks.find { it.id == id }
                                    if (task != null) {
                                        editingTaskId = task.id
                                        title = task.title
                                        description = task.description ?: ""
                                        category = task.category ?: ""
                                        selectedColor = task.color ?: colors[0]
                                    }
                                }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(0.4f).padding(start = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val progress = if (uiState.tasks.isNotEmpty()) doneTasks.size.toFloat() / uiState.tasks.size else 0f
                        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxSize(), color = primaryColor, strokeWidth = 12.dp)
                            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = primaryColor)
                        }
                        Text("Progresso", fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 3. PAINEL DE ADIÇÃO / EDIÇÃO
            Surface(
                modifier = Modifier
                    .width(420.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(32.dp),
                color = surfaceColor.copy(alpha = 0.3f), // Ajuste a transparência aqui (0.3f é bem transparente)
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (editingTaskId == null) "Nova Tarefa" else "Editar Tarefa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    TaskInputField(value = title, onValueChange = { title = it }, label = "O que fazer?", icon = Icons.Default.Create)
                    Spacer(modifier = Modifier.height(20.dp))

                    TaskInputField(value = description, onValueChange = { description = it }, label = "Detalhes", icon = Icons. Default.Edit, isMultiline = true)
                    Spacer(modifier = Modifier.height(32.dp))

                    TaskInputField(value = category, onValueChange = { category = it }, label = "Categoria", icon = Icons.Default.List)
                    Spacer(modifier = Modifier.height(24.dp))

                    ColorPicker(colors = colors, selectedColor = selectedColor, onColorSelected = { selectedColor = it })
                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                if (editingTaskId == null) {
                                    todoViewModel. addTodo(title, description, category, selectedColor, null)
                                } else {
                                    todoViewModel.updateTask(editingTaskId!!, title, description, category, selectedColor, null)
                                }
                                editingTaskId = null
                                title = ""
                                description = ""
                                category = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        enabled = title.isNotBlank()
                    ) {
                        Text(if (editingTaskId == null) "Criar Tarefa" else "Atualizar Tarefa", fontWeight = FontWeight.Bold)
                    }

                    if (editingTaskId != null) {
                        TextButton(
                            onClick = { editingTaskId = null; title = ""; description = ""; category = "" },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Cancelar Edição", color = Color.Gray)
                        }
                    }
                }
            }

        }
    }
}



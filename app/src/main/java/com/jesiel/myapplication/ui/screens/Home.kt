package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.TodoUiState
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.viewmodel.UiEvent
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(todoViewModel: TodoViewModel = viewModel()) {
    val uiState by todoViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        todoViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowUndoSnackbar -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Tarefa removida",
                            actionLabel = "Desfazer",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            todoViewModel.undoDelete(event.task)
                        } else {
                            todoViewModel.confirmDeletion()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(12.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { innerPadding ->
        HomeContent(
            uiState = uiState,
            showSheet = showSheet,
            onDismissSheet = { showSheet = false },
            onSaveTodo = { title, desc, cat, color, reminder ->
                todoViewModel.addTodo(context, title, desc, cat, color, reminder)
            },
            onRefresh = { todoViewModel.refresh() },
            onToggleTaskStatus = { taskId -> todoViewModel.toggleTaskStatus(taskId) },
            onDeleteTask = { taskId -> todoViewModel.deleteTodo(taskId) },
            contentPadding = innerPadding
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    uiState: TodoUiState,
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onSaveTodo: (String, String?, String?, String?, Long?) -> Unit,
    onRefresh: () -> Unit,
    onToggleTaskStatus: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val pullRefreshState = rememberPullRefreshState(uiState.isLoading, onRefresh)
    val randomBackgroundImage = remember { "https://picsum.photos/1000/1800?random=${System.currentTimeMillis()}" }

    val categories = remember(uiState.tasks) {
        listOf("Tudo") + uiState.tasks.mapNotNull { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("Tudo") }

    val filteredTasks = remember(uiState.tasks, selectedCategory) {
        if (selectedCategory == "Tudo") {
            uiState.tasks
        } else {
            uiState.tasks.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        AsyncImage(
            model = randomBackgroundImage,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        )

        if (uiState.isLoading && uiState.tasks.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Header()
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { selectedCategory = category },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        Card(
                            task = task,
                            onToggleStatus = onToggleTaskStatus,
                            onDelete = onDeleteTask
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        ExampleBottomSheet(
            showSheet = showSheet,
            onDismissSheet = onDismissSheet,
            onSave = onSaveTodo
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    myTodosTheme {
        val sampleTasks = listOf(
            Task(id = 1, title = "Wakeup", category = "Rotina", done = false),
            Task(id = 2, title = "Morning exercises", category = "Saúde", done = true),
        )
        HomeContent(
            uiState = TodoUiState(tasks = sampleTasks),
            showSheet = false,
            onDismissSheet = {},
            onSaveTodo = { _, _, _, _, _ -> },
            onRefresh = {},
            onToggleTaskStatus = {},
            onDeleteTask = {}
        )
    }
}

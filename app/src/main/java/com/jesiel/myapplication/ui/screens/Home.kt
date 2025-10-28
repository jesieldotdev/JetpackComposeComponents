package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.TodoUiState
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(todoViewModel: TodoViewModel = viewModel()) {
    val uiState by todoViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        todoViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Tarefa removida",
                        actionLabel = "Desfazer"
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            showSheet = showSheet,
            onDismissSheet = { showSheet = false },
            onSaveTodo = { title -> todoViewModel.addTodo(title) },
            onRefresh = { todoViewModel.refresh() },
            onToggleTaskStatus = { taskId -> todoViewModel.toggleTaskStatus(taskId) },
            onDeleteTask = { taskId -> todoViewModel.deleteTodo(taskId) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: TodoUiState,
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onSaveTodo: (String) -> Unit,
    onRefresh: () -> Unit,
    onToggleTaskStatus: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(uiState.isLoading, onRefresh)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState)
    ) {
        if (uiState.isLoading && uiState.tasks.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                Modifier.padding(16.dp)
            ) {
                Header()
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
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
            Task(id = 1, title = "Wakeup", done = false),
            Task(id = 2, title = "Morning exercises", done = true),
        )
        HomeContent(
            uiState = TodoUiState(tasks = sampleTasks),
            showSheet = false,
            onDismissSheet = {},
            onSaveTodo = {},
            onRefresh = {},
            onToggleTaskStatus = {},
            onDeleteTask = {}
        )
    }
}
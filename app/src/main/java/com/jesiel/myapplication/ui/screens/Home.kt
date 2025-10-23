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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header
import com.jesiel.myapplication.ui.components.Week
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.TodoUiState
import com.jesiel.myapplication.viewmodel.TodoViewModel

@Composable
fun HomeScreen(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    todoViewModel: TodoViewModel = viewModel()
) {
    val uiState by todoViewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        showSheet = showSheet,
        onDismissSheet = onDismissSheet,
        onSaveTodo = { title -> todoViewModel.addTodo(title) },
        onRefresh = { todoViewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    uiState: TodoUiState,
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onSaveTodo: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(uiState.isLoading, onRefresh)

    myTodosTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
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
                    Week()
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.tasks) { task ->
                            Card(task)
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
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    val sampleTasks = listOf(
        Task(id = 1, title = "Wakeup", done = false),
        Task(id = 2, title = "Morning exercises", done = true),
    )
    HomeContent(
        uiState = TodoUiState(tasks = sampleTasks),
        showSheet = false,
        onDismissSheet = {},
        onSaveTodo = {},
        onRefresh = {}
    )
}

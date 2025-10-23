package com.jesiel.myapplication.ui.screens

import Week
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.TodoViewModel

@Composable
fun HomeScreen(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    todoViewModel: TodoViewModel = viewModel()
) {
    val tasks by todoViewModel.tasks.collectAsState()

    HomeContent(
        tasks = tasks,
        showSheet = showSheet,
        onDismissSheet = onDismissSheet
    )
}

@Composable
fun HomeContent(
    tasks: List<Task>,
    showSheet: Boolean,
    onDismissSheet: () -> Unit
) {
    myTodosTheme(dynamicColor = false) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Header()
//                Week()
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks) { task ->
                        Card(task)
                    }
                }

                ExampleBottomSheet(
                    showSheet,
                    onDismissSheet
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    val sampleTasks = listOf(
        Task(
            id = 1,
            title = "Wakeup",
            done = false
        ),
        Task(
            id = 2,
            title = "Morning exercises",
            done = true
        ),
    )
    HomeContent(tasks = sampleTasks, showSheet = false, onDismissSheet = {})
}

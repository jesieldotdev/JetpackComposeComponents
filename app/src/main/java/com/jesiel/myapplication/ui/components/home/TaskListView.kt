package com.jesiel.myapplication.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card

@Composable
fun TaskListView(
    pendingTasks: List<Task>,
    doneTasks: List<Task>,
    onToggleTaskStatus: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Pending tasks section
        items(pendingTasks, key = { it.id }) { task ->
            Card(
                task = task,
                onToggleStatus = onToggleTaskStatus,
                onDelete = onDeleteTask,
                onClick = { onTaskClick(task.id) }
            )
        }

        // 2. "Done" section header and items
        if (doneTasks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Concluídas",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }
            
            items(doneTasks, key = { it.id }) { task ->
                Card(
                    task = task,
                    onToggleStatus = onToggleTaskStatus,
                    onDelete = onDeleteTask,
                    onClick = { onTaskClick(task.id) }
                )
            }
        }
    }
}

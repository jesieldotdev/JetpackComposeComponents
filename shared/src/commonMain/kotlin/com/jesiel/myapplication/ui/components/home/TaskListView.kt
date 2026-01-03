package com.jesiel.myapplication.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.TaskCard

@Composable
fun TaskListView(
    pendingTasks: List<Task>,
    doneTasks: List<Task>,
    onToggleTaskStatus: (Int) -> Unit,
    onTaskClick: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
    ) {
        itemsIndexed(pendingTasks) { index, task ->
            TimelineItem(
                task = task,
                onToggleTaskStatus = onToggleTaskStatus,
                onTaskClick = { onTaskClick(task.id) },
                isFirst = index == 0,
                isLast = index == pendingTasks.lastIndex && doneTasks.isEmpty()
            )
        }

        if (doneTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Concluídas",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 44.dp, top = 24.dp, bottom = 12.dp)
                )
            }
            itemsIndexed(doneTasks) { index, task ->
                TimelineItem(
                    task = task,
                    onToggleTaskStatus = onToggleTaskStatus,
                    onTaskClick = { onTaskClick(task.id) },
                    isFirst = index == 0 && pendingTasks.isEmpty(),
                    isLast = index == doneTasks.lastIndex
                )
            }
        }
    }
}

@Composable
fun TimelineItem(
    task: Task,
    onToggleTaskStatus: (Int) -> Unit,
    onTaskClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val alpha = if (task.done) 0.6f else 1f

    Row(modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth().alpha(alpha)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(44.dp)) {
            Box(Modifier.width(2.dp).height(24.dp).background(if (isFirst) Color.Transparent else accentColor.copy(alpha = 0.3f)))
            Box(
                Modifier.size(18.dp)
                    .clip(CircleShape)
                    .clickable { onToggleTaskStatus(task.id) }
                    .border(2.dp, accentColor, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(if (task.done) accentColor else Color.Transparent)
            )
            Box(Modifier.width(2.dp).weight(1f).background(if (isLast) Color.Transparent else accentColor.copy(alpha = 0.3f)))
        }
        
        Box(modifier = Modifier.padding(bottom = 12.dp).weight(1f)) {
            // USANDO O CARD REAL COM TODAS AS INFORMAÇÕES
            TaskCard(
                task = task,
                onToggleStatus = onToggleTaskStatus,
                onClick = onTaskClick
            )
        }
    }
}

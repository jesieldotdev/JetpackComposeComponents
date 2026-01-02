package com.jesiel.myapplication.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.toColor

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
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Pending tasks section
        itemsIndexed(pendingTasks, key = { _, task -> task.id }) { index, task ->
            TimelineItem(
                task = task,
                onToggleTaskStatus = onToggleTaskStatus,
                onDeleteTask = onDeleteTask,
                onTaskClick = onTaskClick,
                isFirst = index == 0,
                isLast = index == pendingTasks.lastIndex && doneTasks.isEmpty()
            )
        }

        // 2. "Done" section header and items
        if (doneTasks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Concluídas",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 44.dp, bottom = 12.dp)
                )
            }
            
            itemsIndexed(doneTasks, key = { _, task -> task.id }) { index, task ->
                TimelineItem(
                    task = task,
                    onToggleTaskStatus = onToggleTaskStatus,
                    onDeleteTask = onDeleteTask,
                    onTaskClick = onTaskClick,
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
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    val taskColor = task.color?.toColor() ?: MaterialTheme.colorScheme.primary
    val contentAlpha = if (task.done) 0.5f else 1f

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .alpha(contentAlpha)
    ) {
        // Timeline column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(44.dp)
        ) {
            // Line ABOVE circle
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp) // Height to the center of the circle area
                    .background(if (isFirst) Color.Transparent else taskColor.copy(alpha = 0.3f))
            )
            
            // Circle with outer ring effect
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .clickable { onToggleTaskStatus(task.id) }
                    .border(2.dp, taskColor, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(if (task.done) taskColor else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {}
            
            // Line BELOW circle
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f) // Takes the rest of the height
                    .background(if (isLast) Color.Transparent else taskColor.copy(alpha = 0.3f))
            )
        }

        // Card Content
        Box(modifier = Modifier.padding(bottom = 12.dp).weight(1f)) {
            Card(
                task = task,
                onToggleStatus = onToggleTaskStatus,
                onDelete = onDeleteTask,
                onClick = { onTaskClick(task.id) }
            )
        }
    }
}

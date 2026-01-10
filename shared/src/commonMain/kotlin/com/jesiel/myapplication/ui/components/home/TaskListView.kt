package com.jesiel.myapplication.ui.components.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
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
        itemsIndexed(pendingTasks, key = { _, task -> "pending_${task.id}" }) { index, task ->
            TaskSwipeItem(
                task = task,
                onToggleTaskStatus = onToggleTaskStatus,
                onDeleteTask = onDeleteTask,
                onTaskClick = onTaskClick,
                isFirst = index == 0,
                isLast = index == pendingTasks.lastIndex && doneTasks.isEmpty()
            )
        }

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
            
            itemsIndexed(doneTasks, key = { _, task -> "done_${task.id}" }) { index, task ->
                TaskSwipeItem(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSwipeItem(
    task: Task,
    onToggleTaskStatus: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggleTaskStatus(task.id)
                    false // Snap back (concluir)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDeleteTask(task.id)
                    true // Dismiss (deletar)
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val direction = dismissState.targetValue
            val color by animateColorAsState(
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50) // Verde para Concluir
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935) // Vermelho para Deletar
                    else -> Color.Transparent
                }
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> null
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = Color.White, modifier = Modifier.scale(if (direction == SwipeToDismissBoxValue.Settled) 0.7f else 1.2f))
                }
            }
        }
    ) {
        TimelineItem(
            task = task,
            onToggleTaskStatus = onToggleTaskStatus,
            onDeleteTask = onDeleteTask,
            onTaskClick = onTaskClick,
            isFirst = isFirst,
            isLast = isLast
        )
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
    val accentColor = MaterialTheme.colorScheme.primary
    val contentAlpha = if (task.done) 0.5f else 1f

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .alpha(contentAlpha)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(44.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(if (isFirst) Color.Transparent else accentColor.copy(alpha = 0.3f))
            )
            
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .clickable { onToggleTaskStatus(task.id) }
                    .border(2.dp, accentColor, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(if (task.done) accentColor else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {}
            
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(if (isLast) Color.Transparent else accentColor.copy(alpha = 0.3f))
            )
        }

        Box(modifier = Modifier.padding(bottom = 12.dp).weight(1f)) {
            TaskCard(
                task = task,
                onToggleStatus = onToggleTaskStatus,
                onClick = { onTaskClick(task.id) }
            )
        }
    }
}

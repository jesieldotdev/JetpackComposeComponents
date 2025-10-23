package com.jesiel.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.theme.myTodosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Card(task: Task, onToggleStatus: (Int) -> Unit, onDelete: (Int) -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggleStatus(task.id)
                    return@rememberSwipeToDismissBoxState false // Do not dismiss
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(task.id)
                    return@rememberSwipeToDismissBoxState true // Dismiss after delete
                }
                else -> return@rememberSwipeToDismissBoxState false
            }
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { // Corrected: No parameter here
            val direction = dismissState.dismissDirection
            SwipeBackground(direction = direction)
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        TaskContent(task = task)
    }
}

@Composable
private fun TaskContent(task: Task) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskIndicator(isDone = task.done)
        Text(
            text = task.title,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

@Composable
private fun TaskIndicator(isDone: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "indicatorColor"
    )
    Canvas(modifier = Modifier.size(24.dp)) {
        if (isDone) {
            drawCircle(color = color)
        } else {
            drawCircle(color = color, style = Stroke(width = 6f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(direction: SwipeToDismissBoxValue) {
    val (icon, alignment, color) = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Triple(Icons.Default.Check, Alignment.CenterStart, MaterialTheme.colorScheme.primaryContainer)
        SwipeToDismissBoxValue.EndToStart -> Triple(Icons.Default.Delete, Alignment.CenterEnd, MaterialTheme.colorScheme.errorContainer)
        else -> Triple(null, Alignment.Center, Color.Transparent)
    }

    val animatedColor by animateColorAsState(targetValue = color, label = "swipeBackgroundColor")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(animatedColor)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = if (direction == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CardPreview() {
    myTodosTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            TaskContent(
                task = Task(
                    id = 1,
                    title = "Wakeup",
                    done = false
                )
            )
            TaskContent(
                task = Task(
                    id = 2,
                    title = "Morning exercises",
                    done = true
                )
            )
        }
    }
}
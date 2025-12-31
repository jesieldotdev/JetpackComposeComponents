package com.jesiel.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.theme.myTodosTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Helper to convert hex string to Color
fun String.toColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color.Transparent
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Card(task: Task, onToggleStatus: (Int) -> Unit, onDelete: (Int) -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggleStatus(task.id)
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(task.id)
                    return@rememberSwipeToDismissBoxState true
                }
                else -> return@rememberSwipeToDismissBoxState false
            }
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                }, label = "swipeBackgroundColor"
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
            val iconColor = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.onPrimaryContainer
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
                else -> Color.Transparent
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = iconColor)
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        TaskContent(task = task, onToggleStatus = onToggleStatus)
    }
}

@Composable
private fun TaskContent(task: Task, onToggleStatus: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
            .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Color indicator stripe
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .background(task.color?.toColor() ?: Color.Transparent)
        )

        Checkbox(
            checked = task.done,
            onCheckedChange = { onToggleStatus(task.id) },
            modifier = Modifier.padding(start = 8.dp)
        )

        Column (
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = task.title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
            )
            task.description?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Normal,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                task.created?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Normal,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                if (task.category != null && task.category.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Spacer pushes the reminder info to the right
                Spacer(modifier = Modifier.weight(1f))

                // Detailed Reminder Info aligned to the right
                if (task.reminder != null) {
                    val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(task.reminder), ZoneId.systemDefault())
                    val formattedReminder = ldt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = formattedReminder,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    myTodosTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            Card(
                task = Task(
                    id = 1,
                    title = "Wakeup",
                    description = "This is a sample description for the task.",
                    done = false,
                    created = "Oct 23, 07:00",
                    category = "Rotina",
                    color = "#FF5733",
                    reminder = System.currentTimeMillis() + 3600000
                ),
                onToggleStatus = {},
                onDelete = {}
            )
        }
    }
}

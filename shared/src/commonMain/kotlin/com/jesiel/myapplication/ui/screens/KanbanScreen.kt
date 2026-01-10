package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.data.TaskStatus
import com.jesiel.myapplication.ui.components.TaskCard
import com.jesiel.myapplication.viewmodel.TodoUiState
import kotlinx.coroutines.launch

@Composable
fun KanbanScreen(
    uiState: TodoUiState,
    onUpdateStatus: (Int, TaskStatus) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val pendingTasks = uiState.tasks.filter { it.status == TaskStatus.PENDING }.sortedByDescending { it.id }
    val progressTasks = uiState.tasks.filter { it.status == TaskStatus.IN_PROGRESS }.sortedByDescending { it.id }
    val doneTasks = uiState.tasks.filter { it.status == TaskStatus.DONE }.sortedByDescending { it.id }
    
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            divider = {}
        ) {
            val tabs = listOf(
                "Pendentes" to pendingTasks.size,
                "Em andamento" to progressTasks.size,
                "Concluído" to doneTasks.size
            )
            tabs.forEachIndexed { index, (label, count) ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, fontWeight = FontWeight.Bold)
                            Text(
                                text = "($count)", 
                                fontSize = 10.sp, 
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            pageSpacing = 16.dp,
            verticalAlignment = Alignment.Top
        ) { page ->
            val tasks = when(page) {
                0 -> pendingTasks
                1 -> progressTasks
                else -> doneTasks
            }
            
            KanbanColumn(
                tasks = tasks,
                currentStatus = when(page) {
                    0 -> TaskStatus.PENDING
                    1 -> TaskStatus.IN_PROGRESS
                    else -> TaskStatus.DONE
                },
                onUpdateStatus = onUpdateStatus,
                onDeleteTask = onDeleteTask,
                onTaskClick = onTaskClick
            )
        }
    }
}

@Composable
fun KanbanColumn(
    tasks: List<Task>,
    currentStatus: TaskStatus,
    onUpdateStatus: (Int, TaskStatus) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            Box {
                TaskCard(
                    task = task,
                    onToggleStatus = { /* Status alternado via flechas no kanban */ },
                    onClick = { onTaskClick(task.id) }
                )
                
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (currentStatus != TaskStatus.PENDING) {
                        IconButton(
                            onClick = { 
                                val prev = if (currentStatus == TaskStatus.DONE) TaskStatus.IN_PROGRESS else TaskStatus.PENDING
                                onUpdateStatus(task.id, prev)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack, 
                                contentDescription = "Mover para trás",
                                modifier = Modifier.size(16.dp), 
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (currentStatus != TaskStatus.DONE) {
                        IconButton(
                            onClick = { 
                                val next = if (currentStatus == TaskStatus.PENDING) TaskStatus.IN_PROGRESS else TaskStatus.DONE
                                onUpdateStatus(task.id, next)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward, 
                                contentDescription = "Mover para frente",
                                modifier = Modifier.size(16.dp), 
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

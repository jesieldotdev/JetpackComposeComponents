package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesiel.myapplication.data.TaskStatus
import com.jesiel.myapplication.ui.components.AppDrawer
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.home.CategoryFilterBar
import com.jesiel.myapplication.ui.components.home.HomeTopBar
import com.jesiel.myapplication.ui.components.home.TaskListView
import com.jesiel.myapplication.viewmodel.ThemeViewModel
import com.jesiel.myapplication.viewmodel.TodoUiState
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todoViewModel: TodoViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToHabits: () -> Unit = {}
) {
    val uiState by todoViewModel.uiState.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        todoViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowUndoSnackbar -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Tarefa removida",
                            actionLabel = "Desfazer",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            todoViewModel.undoDelete(event.task)
                        } else {
                            todoViewModel.confirmDeletion()
                        }
                    }
                }
                is UiEvent.NavigateToTask -> {
                    onNavigateToDetail(event.taskId)
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    AppDrawer(
                        isKanbanMode = themeState.isKanbanMode,
                        onToggleKanban = {
                            scope.launch { 
                                drawerState.close()
                                themeViewModel.setKanbanMode(!themeState.isKanbanMode)
                            }
                        },
                        onNavigateToHome = { scope.launch { drawerState.close() } },
                        onNavigateToHabits = onNavigateToHabits,
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToAbout = onNavigateToAbout
                    )
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    containerColor = Color.Transparent,
                    snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                actionColor = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showSheet = true },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
                        }
                    }
                ) { innerPadding ->
                    HomeContent(
                        uiState = uiState,
                        isKanbanMode = themeState.isKanbanMode,
                        showSheet = showSheet,
                        onDismissSheet = { showSheet = false },
                        onSaveTodo = { t, d, c, cl, r -> todoViewModel.addTodo(context, t, d, c, cl, r) },
                        onRefresh = { todoViewModel.refresh() },
                        onToggleTaskStatus = { id -> todoViewModel.toggleTaskStatus(context, id) },
                        onUpdateTaskStatus = { id, s -> todoViewModel.updateTaskStatus(context, id, s) },
                        onDeleteTask = { id -> todoViewModel.deleteTodo(id) },
                        onTaskClick = onNavigateToDetail,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onCategorySelect = { todoViewModel.setSelectedCategory(it) },
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    uiState: TodoUiState,
    isKanbanMode: Boolean,
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onSaveTodo: (String, String?, String?, String?, Long?) -> Unit,
    onRefresh: () -> Unit,
    onToggleTaskStatus: (Int) -> Unit,
    onUpdateTaskStatus: (Int, TaskStatus) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    onCategorySelect: (String) -> Unit,
    contentPadding: PaddingValues
) {
    val pullRefreshState = rememberPullRefreshState(uiState.isLoading, onRefresh)
    
    val categories = remember(uiState.tasks) {
        listOf("Tudo") + uiState.tasks.mapNotNull { it.category }.filter { it.isNotBlank() }.distinct()
    }

    val (pendingTasks, doneTasks) = remember(uiState.tasks, uiState.selectedCategory) {
        val filtered = if (uiState.selectedCategory == "Tudo") uiState.tasks else uiState.tasks.filter { it.category == uiState.selectedCategory }
        val sorted = filtered.sortedByDescending { it.id }
        sorted.partition { it.status != TaskStatus.DONE }
    }

    Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        if (uiState.showBackgroundImage) {
            BlurredBackground(imageUrl = uiState.backgroundImageUrl, blurIntensity = uiState.blurIntensity)
        } else {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        }

        if (uiState.isLoading && uiState.tasks.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                HomeTopBar(onMenuClick = onMenuClick)
                CategoryFilterBar(categories, uiState.selectedCategory) { onCategorySelect(it) }
                
                Spacer(modifier = Modifier.height(16.dp))

                if (isKanbanMode) {
                    KanbanScreen(
                        uiState = uiState.copy(tasks = if (uiState.selectedCategory == "Tudo") uiState.tasks else uiState.tasks.filter { it.category == uiState.selectedCategory }),
                        onUpdateStatus = onUpdateTaskStatus,
                        onDeleteTask = onDeleteTask,
                        onTaskClick = onTaskClick
                    )
                } else {
                    TaskListView(pendingTasks, doneTasks, onToggleTaskStatus, onDeleteTask, onTaskClick)
                }
            }
        }

        PullRefreshIndicator(uiState.isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter)
        )

        ExampleBottomSheet(showSheet, onDismissSheet, null, onSaveTodo)
    }
}

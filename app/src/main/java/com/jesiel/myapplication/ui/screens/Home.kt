package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header
import com.jesiel.myapplication.ui.theme.myTodosTheme
import com.jesiel.myapplication.viewmodel.TodoUiState
import com.jesiel.myapplication.viewmodel.TodoViewModel
import com.jesiel.myapplication.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todoViewModel: TodoViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val uiState by todoViewModel.uiState.collectAsState()
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
            }
        }
    }

    // Wrap the drawer in a provider to flip its direction to Right (RTL)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // Inside the drawer, restore Left-to-Right direction for content
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                        // Rounded corners on the LEFT side because it opens from the right
                        drawerShape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp),
                        modifier = Modifier.width(300.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "myTodos",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Gerencie sua rotina com elegância",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp), 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DrawerItem(
                            label = "Configurações",
                            icon = Icons.Default.Settings,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToSettings()
                            }
                        )
                        
                        DrawerItem(
                            label = "Sobre",
                            icon = Icons.Default.Info,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToAbout()
                            }
                        )
                    }
                }
            }
        ) {
            // Restore Left-to-Right direction for the main app content
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
                        showSheet = showSheet,
                        onDismissSheet = { showSheet = false },
                        onSaveTodo = { title, desc, cat, color, reminder ->
                            todoViewModel.addTodo(context, title, desc, cat, color, reminder)
                        },
                        onRefresh = { todoViewModel.refresh() },
                        onToggleTaskStatus = { taskId -> todoViewModel.toggleTaskStatus(context, taskId) },
                        onDeleteTask = { taskId -> todoViewModel.deleteTodo(taskId) },
                        onTaskClick = onNavigateToDetail,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = label, fontWeight = FontWeight.Medium) },
        selected = false,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    uiState: TodoUiState,
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onSaveTodo: (String, String?, String?, String?, Long?) -> Unit,
    onRefresh: () -> Unit,
    onToggleTaskStatus: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val pullRefreshState = rememberPullRefreshState(uiState.isLoading, onRefresh)
    val context = LocalContext.current
    
    val categories = remember(uiState.tasks) {
        listOf("Tudo") + uiState.tasks.mapNotNull { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("Tudo") }

    val filteredTasks = remember(uiState.tasks, selectedCategory) {
        if (selectedCategory == "Tudo") {
            uiState.tasks
        } else {
            uiState.tasks.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uiState.backgroundImageUrl)
                .crossfade(true)
                .diskCacheKey(uiState.backgroundImageUrl)
                .memoryCacheKey(uiState.backgroundImageUrl)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        )

        if (uiState.isLoading && uiState.tasks.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Header()
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onMenuClick() },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Menu, 
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { selectedCategory = category },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        Card(
                            task = task,
                            onToggleStatus = { taskId -> onToggleTaskStatus(taskId) },
                            onDelete = onDeleteTask,
                            onClick = { onTaskClick(task.id) }
                        )
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

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    myTodosTheme {
        val sampleTasks = listOf(
            Task(id = 1, title = "Wakeup", category = "Rotina", done = false),
            Task(id = 2, title = "Morning exercises", category = "Saúde", done = true),
        )
        HomeContent(
            uiState = TodoUiState(tasks = sampleTasks),
            showSheet = false,
            onDismissSheet = {},
            onSaveTodo = { _, _, _, _, _ -> },
            onRefresh = {},
            onToggleTaskStatus = {},
            onDeleteTask = {},
            onTaskClick = {},
            onMenuClick = {}
        )
    }
}

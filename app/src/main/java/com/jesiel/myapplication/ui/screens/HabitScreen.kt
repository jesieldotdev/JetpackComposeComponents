package com.jesiel.myapplication.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.data.HabitPeriod
import com.jesiel.myapplication.ui.components.AppDrawer
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.form.HabitBottomSheet
import com.jesiel.myapplication.ui.components.home.HomeTopBar
import com.jesiel.myapplication.ui.components.toColor
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.ThemeViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HabitScreen(
    habitViewModel: HabitViewModel = viewModel(),
    todoViewModel: TodoViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHabitDetail: (Int) -> Unit
) {
    val habitState by habitViewModel.uiState.collectAsState()
    val todoState by todoViewModel.uiState.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddHabitSheet by remember { mutableStateOf(false) }

    // Pull Refresh State
    val pullRefreshState = rememberPullRefreshState(
        refreshing = habitState.isLoading,
        onRefresh = { habitViewModel.fetchHabits() }
    )

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
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToHabits = { scope.launch { drawerState.close() } },
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToAbout = onNavigateToAbout
                    )
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState) // Enabled Pull Refresh
                ) {
                    if (todoState.showBackgroundImage) {
                        BlurredBackground(
                            imageUrl = todoState.backgroundImageUrl,
                            blurIntensity = todoState.blurIntensity
                        )
                    } else {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                    }

                    Scaffold(
                        containerColor = Color.Transparent,
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { showAddHabitSheet = true },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Add, "Novo Hábito")
                            }
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            HomeTopBar(onMenuClick = { scope.launch { drawerState.open() } })
                            
                            Text(
                                text = "Meus Hábitos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (habitState.isLoading && habitState.habits.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(habitState.habits, key = { it.id }) { habit ->
                                        HabitSwipeItem(
                                            habit = habit,
                                            onIncrement = { habitViewModel.incrementHabit(habit.id) },
                                            onClick = { onNavigateToHabitDetail(habit.id) },
                                            onDelete = { habitViewModel.deleteHabit(habit.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Pull Refresh Indicator
                    PullRefreshIndicator(
                        refreshing = habitState.isLoading,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    )

                    HabitBottomSheet(
                        showSheet = showAddHabitSheet,
                        onDismissSheet = { showAddHabitSheet = false },
                        onSave = { title, goal, unit, color, streak, streakGoal, period ->
                            habitViewModel.addHabit(title, goal, unit, color, streakGoal, period)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitSwipeItem(
    habit: Habit,
    onIncrement: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val alignment = Alignment.CenterEnd
            val color = MaterialTheme.colorScheme.errorContainer
            val icon = Icons.Default.Delete

            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        HabitCard(habit = habit, onIncrement = onIncrement, onClick = onClick)
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onIncrement: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = habit.currentProgress >= habit.goal
    val progress = if (habit.goal > 0) habit.currentProgress.toFloat() / habit.goal else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "habitProgress")
    
    val containerColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f) 
                      else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        label = "containerColor"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        tonalElevation = if (isCompleted) 4.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (habit.streak > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔥 ${habit.streak}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                val periodText = when(habit.period) {
                    HabitPeriod.DAILY -> "diário"
                    HabitPeriod.WEEKLY -> "semanal"
                    HabitPeriod.MONTHLY -> "mensal"
                }
                Text(
                    text = "${habit.currentProgress}/${habit.goal} ${habit.unit} ($periodText)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = if (isCompleted) MaterialTheme.colorScheme.primary 
                            else (habit.color?.toColor() ?: MaterialTheme.colorScheme.primary),
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluído",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = (habit.color?.toColor() ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Incrementar",
                        tint = habit.color?.toColor() ?: MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

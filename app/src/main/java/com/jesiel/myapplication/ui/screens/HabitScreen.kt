package com.jesiel.myapplication.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import com.jesiel.myapplication.ui.components.AppDrawer
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.form.HabitBottomSheet
import com.jesiel.myapplication.ui.components.home.HomeTopBar
import com.jesiel.myapplication.ui.components.toColor
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.ThemeViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(
    habitViewModel: HabitViewModel = viewModel(),
    todoViewModel: TodoViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val habitState by habitViewModel.uiState.collectAsState()
    val todoState by todoViewModel.uiState.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddHabitSheet by remember { mutableStateOf(false) }

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
                Box(modifier = Modifier.fillMaxSize()) {
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

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(habitState.habits, key = { it.id }) { habit ->
                                    HabitCard(
                                        habit = habit,
                                        onIncrement = { habitViewModel.incrementHabit(habit.id) }
                                    )
                                }
                            }
                        }
                    }
                    
                    HabitBottomSheet(
                        showSheet = showAddHabitSheet,
                        onDismissSheet = { showAddHabitSheet = false },
                        onSave = { title, goal, unit, color ->
                            habitViewModel.addHabit(title, goal, unit, color)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onIncrement: () -> Unit
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
        modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = "${habit.currentProgress}/${habit.goal} ${habit.unit}",
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

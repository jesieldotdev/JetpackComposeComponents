package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.components.detail.DetailTopBar
import com.jesiel.myapplication.ui.components.form.HabitBottomSheet
import com.jesiel.myapplication.ui.components.habit.WeeklyProgressRow
import com.jesiel.myapplication.ui.components.hexToColor
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Int,
    habitViewModel: HabitViewModel,
    todoViewModel: TodoViewModel,
    onNavigateBack: () -> Unit
) {
    val habitState by habitViewModel.uiState.collectAsState()
    val todoState by todoViewModel.uiState.collectAsState()
    val habit = habitState.habits.find { it.id == habitId }
    var showEditSheet by remember { mutableStateOf(false) }

    if (habit == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (todoState.showBackgroundImage) {
            BlurredBackground(
                imageUrl = todoState.backgroundImageUrl,
                blurIntensity = todoState.blurIntensity,
                scrimAlpha = 0.6f
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { 
                DetailTopBar(
                    title = "Detalhes do Hábito",
                    onNavigateBack = onNavigateBack
                ) 
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showEditSheet = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Hábito")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp, top = 24.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (todoState.showBackgroundImage) 0.95f else 1f),
                        tonalElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(habit.color?.let { hexToColor(it) } ?: MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = habit.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Text("Meta Diária", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Text("${habit.currentProgress} / ${habit.goal} ${habit.unit}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val progress = if (habit.goal > 0) habit.currentProgress.toFloat() / habit.goal else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                                color = habit.color?.let { hexToColor(it) } ?: MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 32.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Streak Diário", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔥 ${habit.streak} dias", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100))
                                        if (habit.streakGoal > 0 && habit.streak >= habit.streakGoal) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.Star, "Meta Atingida!", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                            
                            if (habit.streakGoal > 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                                val streakProgress = (habit.streak.toFloat() / habit.streakGoal).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { streakProgress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = Color(0xFFE65100),
                                    trackColor = Color(0xFFE65100).copy(alpha = 0.1f)
                                )

                                if (habit.streak >= habit.streakGoal) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { habitViewModel.startNewOffensive(habit.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100), contentColor = Color.White),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Começar Nova Ofensiva", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            Text("Progresso da Semana", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(12.dp))
                            WeeklyProgressRow(habit)
                        }
                    }
                }

                if (habit.pastOffensives.isNotEmpty()) {
                    items(habit.pastOffensives.reversed()) { streakValue ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (todoState.showBackgroundImage) 0.9f else 1f),
                            tonalElevation = 1.dp
                        ) {
                            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE65100).copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                        Text("🔥", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Ofensiva Concluída", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("$streakValue dias", fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100))
                            }
                        }
                    }
                }
            }
        }

        HabitBottomSheet(
            showSheet = showEditSheet,
            onDismissSheet = { showEditSheet = false },
            initialHabit = habit,
            onSave = { title, goal, unit, color, streak, streakGoal, period ->
                habitViewModel.updateHabit(habit.id, title, goal, unit, color, streak, streakGoal, period)
                showEditSheet = false
            }
        )
    }
}

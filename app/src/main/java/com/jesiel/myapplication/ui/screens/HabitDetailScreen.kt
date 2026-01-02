package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.detail.DetailTopBar
import com.jesiel.myapplication.ui.components.form.HabitBottomSheet
import com.jesiel.myapplication.ui.components.toColor
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        BlurredBackground(
            imageUrl = todoState.backgroundImageUrl,
            blurIntensity = todoState.blurIntensity,
            scrimAlpha = 0.6f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { DetailTopBar(onNavigateBack) },
            floatingActionButton = {
                FloatingActionButton(onClick = { showEditSheet = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Hábito")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(habit.color?.toColor() ?: MaterialTheme.colorScheme.primary)
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

                        // Progress Section
                        Text(
                            "Meta Diária", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            "${habit.currentProgress} / ${habit.goal} ${habit.unit}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val progress = if (habit.goal > 0) habit.currentProgress.toFloat() / habit.goal else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                            color = habit.color?.toColor() ?: MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 32.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                        )

                        // Streak Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Streak Diário", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "🔥 ${habit.streak} dias", 
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE65100)
                                    )
                                    if (habit.streakGoal > 0 && habit.streak >= habit.streakGoal) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Default.Star, "Meta Atingida!", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                            
                            if (habit.streakGoal > 0) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Meta", 
                                        style = MaterialTheme.typography.labelSmall, 
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        "${habit.streakGoal} dias",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
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
                        }
                    }
                }
            }
        }

        HabitBottomSheet(
            showSheet = showEditSheet,
            onDismissSheet = { showEditSheet = false },
            initialHabit = habit,
            onSave = { title, goal, unit, color, streak, streakGoal ->
                habitViewModel.updateHabit(habit.id, title, goal, unit, color, streak, streakGoal)
                showEditSheet = false
            }
        )
    }
}

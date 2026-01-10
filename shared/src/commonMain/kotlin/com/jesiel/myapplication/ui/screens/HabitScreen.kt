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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.data.HabitPeriod
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication.ui.components.home.CategoryFilterBar
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel

@Composable
fun HabitScreen(
    habitViewModel: HabitViewModel,
    todoViewModel: TodoViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHabitDetail: (Int) -> Unit = {}
) {
    val habitState by habitViewModel.uiState.collectAsState()
    val todoState by todoViewModel.uiState.collectAsState()
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(modifier = Modifier.fillMaxSize()) {
        if (todoState.showBackgroundImage) {
            BlurredBackground(
                imageUrl = todoState.backgroundImageUrl,
                blurIntensity = todoState.blurIntensity,
                scrimAlpha = 0.8f
            )
        }

        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            
            // 1. SIDEBAR (Consistente com a Home)
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(primaryColor, MaterialTheme.colorScheme.secondary)))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = onNavigateToHome) { Icon(Icons.Default.Check, null, tint = Color.White.copy(alpha = 0.7f)) }
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
                IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, null, tint = Color.White.copy(alpha = 0.7f)) }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ExitToApp, null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. CONTEÚDO PRINCIPAL
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(surfaceColor.copy(alpha = 0.95f))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Meus Hábitos", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Mantenha sua rotina em dia", color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { /* showAddHabitDialog = true */ },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Novo Hábito")
                    }
                }

                Spacer(Modifier.height(32.dp))

                if (habitState.isLoading && habitState.habits.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(habitState.habits, key = { it.id }) { habit ->
                            HabitCardShared(
                                habit = habit,
                                onIncrement = { habitViewModel.incrementHabit(habit.id) },
                                onClick = { onNavigateToHabitDetail(habit.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitCardShared(
    habit: Habit,
    onIncrement: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = habit.currentProgress >= habit.goal
    val progress = if (habit.goal > 0) habit.currentProgress.toFloat() / habit.goal else 0f
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${habit.currentProgress}/${habit.goal} ${habit.unit}", 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(Modifier.width(24.dp))
            
            if (isCompleted) {
                Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
            } else {
                FilledTonalIconButton(onClick = onIncrement, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    }
}

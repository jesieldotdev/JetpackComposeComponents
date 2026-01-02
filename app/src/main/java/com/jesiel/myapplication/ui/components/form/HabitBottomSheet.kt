package com.jesiel.myapplication.ui.components.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.data.HabitPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitBottomSheet(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    initialHabit: Habit? = null,
    onSave: (String, Int, String, String?, Int, Int, HabitPeriod) -> Unit // Added period parameter
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var streak by remember { mutableStateOf("0") }
    var streakGoal by remember { mutableStateOf("0") }
    var selectedPeriod by remember { mutableStateOf(HabitPeriod.DAILY) }
    
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3")
    var selectedColor by remember { mutableStateOf(colors[0]) }

    LaunchedEffect(initialHabit, showSheet) {
        if (showSheet) {
            if (initialHabit != null) {
                title = initialHabit.title
                goal = initialHabit.goal.toString()
                unit = initialHabit.unit
                streak = initialHabit.streak.toString()
                streakGoal = initialHabit.streakGoal.toString()
                selectedColor = initialHabit.color ?: colors[0]
                selectedPeriod = initialHabit.period
            } else {
                title = ""
                goal = ""
                unit = ""
                streak = "0"
                streakGoal = "0"
                selectedColor = colors[0]
                selectedPeriod = HabitPeriod.DAILY
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialHabit == null) "Novo Hábito" else "Editar Hábito",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TaskInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Qual hábito você quer criar?",
                    icon = Icons.Default.Create
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = goal,
                        onValueChange = { if (it.all { char -> char.isDigit() }) goal = it },
                        label = { Text("Meta") },
                        leadingIcon = { Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidade") },
                        placeholder = { Text("ex: L, min") },
                        leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Period Selection
                Text("Frequência", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabitPeriod.values().forEach { period ->
                        val isSelected = selectedPeriod == period
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPeriod = period },
                            label = { 
                                Text(when(period) {
                                    HabitPeriod.DAILY -> "Diário"
                                    HabitPeriod.WEEKLY -> "Semanal"
                                    HabitPeriod.MONTHLY -> "Mensal"
                                })
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = streak,
                        onValueChange = { if (it.all { char -> char.isDigit() }) streak = it },
                        label = { Text("Streak Atual") },
                        leadingIcon = { Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = streakGoal,
                        onValueChange = { if (it.all { char -> char.isDigit() }) streakGoal = it },
                        label = { Text("Meta de Dias") },
                        leadingIcon = { Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                ColorPicker(
                    colors = colors,
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = {
                        val goalInt = goal.toIntOrNull() ?: 0
                        val streakInt = streak.toIntOrNull() ?: 0
                        val streakGoalInt = streakGoal.toIntOrNull() ?: 0
                        if (title.isNotBlank() && goalInt > 0 && unit.isNotBlank()) {
                            onSave(title, goalInt, unit, selectedColor, streakInt, streakGoalInt, selectedPeriod)
                            onDismissSheet()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = if (initialHabit == null) "Começar Hábito" else "Salvar Alterações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

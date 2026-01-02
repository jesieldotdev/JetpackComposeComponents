package com.jesiel.myapplication.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitBottomSheet(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onSave: (String, Int, String, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3")
    var selectedColor by remember { mutableStateOf(colors[0]) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Novo Hábito",
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
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
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
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
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
                        if (title.isNotBlank() && goalInt > 0 && unit.isNotBlank()) {
                            onSave(title, goalInt, unit, selectedColor)
                            title = ""
                            goal = ""
                            unit = ""
                            onDismissSheet()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Começar Hábito", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

package com.jesiel.myapplication.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.viewmodel.ReminderReceiver
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleBottomSheet(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    initialTask: Task? = null, // Task to pre-fill the form for editing
    onSave: (String, String?, String?, String?, Long?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3")
    var selectedColor by remember { mutableStateOf(colors[0]) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    
    val context = LocalContext.current

    // Pre-fill fields when an initial task is provided
    LaunchedEffect(initialTask, showSheet) {
        if (showSheet) {
            if (initialTask != null) {
                title = initialTask.title
                description = initialTask.description ?: ""
                category = initialTask.category ?: ""
                selectedColor = initialTask.color ?: colors[0]
                reminderTime = initialTask.reminder
            } else {
                title = ""
                description = ""
                category = ""
                selectedColor = colors[0]
                reminderTime = null
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Ensure content is scrollable
            ) {
                Text(
                    text = if (initialTask == null) "Nova Tarefa" else "Editar Tarefa",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val currentDateTime = Calendar.getInstance()
                            if (reminderTime != null) {
                                currentDateTime.timeInMillis = reminderTime!!
                            }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val calendar = Calendar.getInstance().apply {
                                                set(year, month, day, hour, minute)
                                            }
                                            reminderTime = calendar.timeInMillis
                                            
                                            // Quick confirmation notification
                                            val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderTime!!), ZoneId.systemDefault())
                                            val formattedTime = ldt.format(DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm"))
                                            val testIntent = Intent(context, ReminderReceiver::class.java).apply {
                                                putExtra("task_title", "Lembrete definido para $formattedTime")
                                            }
                                            context.sendBroadcast(testIntent)
                                        },
                                        currentDateTime.get(Calendar.HOUR_OF_DAY),
                                        currentDateTime.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                currentDateTime.get(Calendar.YEAR),
                                currentDateTime.get(Calendar.MONTH),
                                currentDateTime.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications, 
                        contentDescription = null, 
                        tint = if (reminderTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (reminderTime != null) {
                            val ldt = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(reminderTime!!), ZoneId.systemDefault())
                            "Lembrete: ${ldt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}"
                        } else {
                            "Adicionar lembrete"
                        },
                        color = if (reminderTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Escolha uma cor:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 0.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(
                                title, 
                                if (description.isBlank()) null else description,
                                if (category.isBlank()) null else category,
                                selectedColor,
                                reminderTime
                            )
                            onDismissSheet()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (initialTask == null) "Salvar" else "Atualizar")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

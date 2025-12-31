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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
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
    initialTask: Task? = null,
    onSave: (String, String?, String?, String?, Long?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3")
    var selectedColor by remember { mutableStateOf(colors[0]) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    
    val context = LocalContext.current

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
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            // Use surfaceVariant for a more modern, integrated background color
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialTask == null) "Nova Tarefa" else "Editar Tarefa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("O que você vai fazer?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição detalhada (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 5,
                    maxLines = 10,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria (ex: Trabalho, Casa)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
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
                        },
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications, 
                            contentDescription = null, 
                            tint = if (reminderTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (reminderTime != null) {
                                val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderTime!!), ZoneId.systemDefault())
                                "Lembrete: ${ldt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}"
                            } else {
                                "Adicionar um lembrete"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (reminderTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Identidade Visual (Cor):", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 0.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (initialTask == null) "Criar Tarefa" else "Salvar Alterações",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

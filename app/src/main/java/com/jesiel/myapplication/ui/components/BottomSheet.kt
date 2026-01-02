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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    text = if (initialTask == null) "Nova Tarefa" else "Editar Tarefa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TaskInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "O que você vai fazer?",
                    icon = Icons.Default.Create
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                TaskInputField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Adicionar detalhes (opcional)",
                    icon = Icons.Default.Edit,
                    isMultiline = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Mais Detalhes", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                TaskInputField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Categoria (ex: Trabalho)",
                    icon = Icons.Default.List
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsActionItem(
                    label = if (reminderTime != null) {
                        val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderTime!!), ZoneId.systemDefault())
                        "Lembrete: ${ldt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}"
                    } else {
                        "Adicionar um lembrete"
                    },
                    icon = Icons.Default.Notifications,
                    isActive = reminderTime != null,
                    onClick = {
                        val currentDateTime = Calendar.getInstance()
                        if (reminderTime != null) currentDateTime.timeInMillis = reminderTime!!
                        DatePickerDialog(context, { _, y, m, d ->
                            TimePickerDialog(context, { _, hh, mm ->
                                val cal = Calendar.getInstance().apply { set(y, m, d, hh, mm) }
                                reminderTime = cal.timeInMillis
                                
                                val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderTime!!), ZoneId.systemDefault())
                                val formatted = ldt.format(DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm"))
                                val testIntent = Intent(context, ReminderReceiver::class.java).apply {
                                    putExtra("task_title", "Lembrete definido para $formatted")
                                }
                                context.sendBroadcast(testIntent)
                            }, currentDateTime.get(Calendar.HOUR_OF_DAY), currentDateTime.get(Calendar.MINUTE), true).show()
                        }, currentDateTime.get(Calendar.YEAR), currentDateTime.get(Calendar.MONTH), currentDateTime.get(Calendar.DAY_OF_MONTH)).show()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Prioridade Visual (Cor)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    colors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
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

                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(title, if (description.isBlank()) null else description, if (category.isBlank()) null else category, selectedColor, reminderTime)
                            onDismissSheet()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = if (initialTask == null) "Criar Tarefa" else "Salvar Alterações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun TaskInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isMultiline: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        singleLine = !isMultiline,
        minLines = if (isMultiline) 4 else 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun SettingsActionItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable { onClick() },
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium, color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

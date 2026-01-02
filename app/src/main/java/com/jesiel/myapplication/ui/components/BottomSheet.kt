package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.components.form.ColorPicker
import com.jesiel.myapplication.ui.components.form.ReminderSelector
import com.jesiel.myapplication.ui.components.form.TaskInputField

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
                
                ReminderSelector(
                    context = context,
                    reminderTime = reminderTime,
                    onReminderSelected = { reminderTime = it }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                ColorPicker(
                    colors = colors,
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

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

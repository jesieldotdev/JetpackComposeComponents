package com.jesiel.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.toColor
import com.jesiel.myapplication.viewmodel.TodoViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    todoViewModel: TodoViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by todoViewModel.uiState.collectAsState()
    val task = uiState.tasks.find { it.id == taskId }
    var showEditSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (task == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = uiState.backgroundImageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(uiState.blurIntensity.dp), // Observed from state
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Detalhes da Tarefa", color = MaterialTheme.colorScheme.onSurface) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showEditSheet = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Tarefa")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
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
                                    .background(task.color?.toColor() ?: Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!task.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                lineHeight = 24.sp
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 24.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Criada em", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = task.created ?: "--", 
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!task.category.isNullOrBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = task.category,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        if (task.reminder != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(task.reminder), ZoneId.systemDefault())
                            val formattedReminder = ldt.format(DateTimeFormatter.ofPattern("dd 'de' MMMM, HH:mm"))
                            
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(18.dp), 
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Lembrete: $formattedReminder",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        ExampleBottomSheet(
            showSheet = showEditSheet,
            onDismissSheet = { showEditSheet = false },
            initialTask = task,
            onSave = { title, desc, cat, color, reminder ->
                todoViewModel.updateTask(context, task.id, title, desc, cat, color, reminder)
                showEditSheet = false
            }
        )
    }
}

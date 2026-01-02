package com.jesiel.myapplication.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun getDayOfWeekText(): String {
    val currentDate = LocalDate.now()
    val locale = Locale("pt", "BR")
    return currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }
}

fun getGreetingText(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    todoViewModel: TodoViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel()
) {
    val todoState by todoViewModel.uiState.collectAsState()
    val habitState by habitViewModel.uiState.collectAsState()

    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = LocalDateTime.now()
            delay(1000 * 60)
        }
    }

    val formattedInfo = remember(currentDateTime) {
        val locale = Locale("pt", "BR")
        val datePart = currentDateTime.format(DateTimeFormatter.ofPattern("E, d 'de' MMM", locale))
        val timePart = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        "${getGreetingText()} • ${datePart.replaceFirstChar { it.uppercase() }} • $timePart"
    }

    // Lista de mensagens individuais
    val messages = remember(todoState.tasks, habitState.habits) {
        val list = mutableListOf<String>()
        
        val now = System.currentTimeMillis()
        val nextReminder = todoState.tasks
            .filter { !it.done && it.reminder != null && it.reminder > now }
            .minByOrNull { it.reminder!! }
        
        if (nextReminder != null) {
            val reminderDT = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(nextReminder.reminder!!),
                ZoneId.systemDefault()
            )
            val dayName = reminderDT.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
            val timeStr = reminderDT.format(DateTimeFormatter.ofPattern("HH:mm"))
            val dateStr = reminderDT.format(DateTimeFormatter.ofPattern("dd/MM"))
            list.add("Você tem um lembrete para: ${nextReminder.title} ($dayName, $dateStr às $timeStr)")
        }

        val pendingCount = todoState.tasks.count { !it.done }
        if (pendingCount > 0) list.add("Você tem $pendingCount tarefas pendentes")
        
        val habitsRemaining = habitState.habits.count { it.currentProgress < it.goal }
        if (habitsRemaining > 0) list.add("Faltam $habitsRemaining hábitos para hoje")
        else if (habitState.habits.isNotEmpty()) list.add("Todos os hábitos concluídos! 🔥")

        if (list.isEmpty()) listOf("Tudo em dia por aqui!") else list
    }

    var showDay by remember { mutableStateOf(true) }
    var currentMessageIndex by remember { mutableIntStateOf(0) }

    // Ciclo: Dia (4s) -> Mensagem[i] (7s) -> Dia (4s) -> Mensagem[i+1]...
    LaunchedEffect(messages) {
        while (true) {
            showDay = true
            delay(6000)
            showDay = false
            delay(12000)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    Column(modifier = modifier) {
        Text(
            text = formattedInfo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        
        Box(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedContent(
                // O targetState agora identifica unicamente se é Dia ou qual Mensagem específica
                targetState = if (showDay) "day" else "msg_$currentMessageIndex",
                transitionSpec = {
                    (fadeIn() + slideInVertically { it }).togetherWith(fadeOut() + slideOutVertically { -it })
                },
                label = "HeaderTransition"
            ) { state ->
                if (state == "day") {
                    Text(
                        text = getDayOfWeekText(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    val currentText = messages.getOrNull(currentMessageIndex) ?: ""
                    Text(
                        text = "• $currentText",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 45.dp
                            )
                    )
                }
            }
        }
    }
}

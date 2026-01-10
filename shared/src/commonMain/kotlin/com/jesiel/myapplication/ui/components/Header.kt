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
import com.jesiel.myapplication.viewmodel.HabitViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.*

@Composable
fun Header(
    modifier: Modifier = Modifier,
    todoViewModel: TodoViewModel,
    habitViewModel: HabitViewModel
) {
    val todoState by todoViewModel.uiState.collectAsState()
    val habitState by habitViewModel.uiState.collectAsState()

    var currentTime by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            delay(1000 * 60)
        }
    }

    val greeting = when (currentTime.hour) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }

    val dayOfWeekText = when (currentTime.dayOfWeek.name) {
        "MONDAY" -> "Segunda-feira"
        "TUESDAY" -> "Terça-feira"
        "WEDNESDAY" -> "Quarta-feira"
        "THURSDAY" -> "Quinta-feira"
        "FRIDAY" -> "Sexta-feira"
        "SATURDAY" -> "Sábado"
        "SUNDAY" -> "Domingo"
        else -> ""
    }

    val formattedInfo = "$greeting • ${currentTime.dayOfMonth}/${currentTime.monthNumber}"

    val messages = remember(todoState.tasks, habitState.habits) {
        val list = mutableListOf<String>()
        val pendingCount = todoState.tasks.count { !it.done }
        if (pendingCount > 0) list.add("Você tem $pendingCount tarefas pendentes")
        
        val habitsRemaining = habitState.habits.count { it.currentProgress < it.goal }
        if (habitsRemaining > 0) list.add("Faltam $habitsRemaining hábitos para hoje")
        else if (habitState.habits.isNotEmpty()) list.add("Todos os hábitos concluídos! 🔥")

        if (list.isEmpty()) listOf("Tudo em dia por aqui!") else list
    }

    var showDay by remember { mutableStateOf(true) }
    var currentMessageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(messages) {
        while (true) {
            showDay = true
            delay(6000)
            showDay = false
            delay(12000)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = formattedInfo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        
        Box(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedContent(
                targetState = if (showDay) "day" else "msg_$currentMessageIndex",
                transitionSpec = {
                    (fadeIn() + slideInVertically { it }).togetherWith(fadeOut() + slideOutVertically { -it })
                },
                label = "HeaderTransition"
            ) { state ->
                if (state == "day") {
                    Text(
                        text = dayOfWeekText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
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
                            .basicMarquee(iterations = Int.MAX_VALUE, velocity = 45.dp)
                    )
                }
            }
        }
    }
}



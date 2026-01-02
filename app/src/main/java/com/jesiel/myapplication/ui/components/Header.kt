package com.jesiel.myapplication.ui.components

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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun getFormattedDate(): String {
    val currentDate = LocalDate.now()
    val locale = Locale("pt", "BR")
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", locale)
    return currentDate.format(formatter)
}

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

    // Consolida todas as informações em uma única string longa para o letreiro
    val marqueeText = remember(todoState.tasks, habitState.habits) {
        val infos = mutableListOf<String>()
        
        val pendingCount = todoState.tasks.count { !it.done }
        if (pendingCount > 0) {
            infos.add("você tem $pendingCount tarefas pendentes")
        }

        val habitsRemaining = habitState.habits.count { it.currentProgress < it.goal }
        if (habitsRemaining > 0) {
            infos.add("faltam $habitsRemaining hábitos para hoje")
        } else if (habitState.habits.isNotEmpty()) {
            infos.add("todos os hábitos concluídos! 🔥")
        }

        if (infos.isEmpty()) "tudo em dia por aqui!" else infos.joinToString("  •  ")
    }

    Column(modifier = modifier) {
        // Linha superior: Saudação e Data
        Text(
            text = "${getGreetingText()} • ${getFormattedDate()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        
        // Linha inferior: Dia da semana + Letreiro Horizontal
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getDayOfWeekText(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.wrapContentWidth()
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // O letreiro (Marquee)
            Text(
                text = "  •  $marqueeText  •  $marqueeText", // Duplicado para efeito contínuo
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        velocity = 40.dp // Velocidade constante "letra por letra"
                    )
            )
        }
    }
}

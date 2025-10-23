package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.theme.myTodosTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

fun getFormattedDate(): String {
    val currentDate = LocalDate.now()
    // Define o Locale para português do Brasil
    val locale = java.util.Locale("pt", "BR")
    // Cria um formatador para "Mês dia, ano" e capitaliza a primeira letra do mês
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", locale)
    return currentDate.format(formatter).replaceFirstChar { it.uppercase() }
}

// Função para obter o dia da semana ("Hoje", "Amanhã" ou o nome do dia)
fun getDayOfWeekText(): String {
    val currentDate = LocalDate.now()
    val locale = java.util.Locale("pt", "BR")
    // Retorna o nome completo do dia da semana (ex: "segunda-feira")
    return currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }
}

@Composable
fun Header() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = getFormattedDate(),
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            text = getDayOfWeekText(),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        )
    }

}

@Preview
@Composable
fun HeaderPreview() {
    myTodosTheme {
        Header()
    }
}

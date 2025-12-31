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
    val locale = java.util.Locale("pt", "BR")
    val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy", locale)
    return currentDate.format(formatter).replaceFirstChar { it.uppercase() }
}

fun getDayOfWeekText(): String {
    val currentDate = LocalDate.now()
    val locale = java.util.Locale("pt", "BR")
    return currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }
}

@Composable
fun Header() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = getFormattedDate(),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Subtitle color
        )
        Text(
            text = getDayOfWeekText(),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface // Adapts to light/dark for better contrast
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

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
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Abstrações simplificadas enquanto não movemos os ViewModels reais
@Composable
fun Header(
    modifier: Modifier = Modifier
) {
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
        
        val hour = currentDateTime.hour
        val greeting = when (hour) {
            in 5..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
        
        "$greeting • ${datePart.replaceFirstChar { it.uppercase() }} • $timePart"
    }

    val dayOfWeek = remember(currentDateTime) {
        val locale = Locale("pt", "BR")
        currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }
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
            Text(
                text = dayOfWeek,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.theme.myTodosTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Week() {
    // Translated and abbreviated days of the week in Portuguese
    val daysOfWeek = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    val today = LocalDate.now().dayOfWeek.value - 1 // Monday is 1, so we subtract 1 to get index 0
    var selectedIndex by remember { mutableStateOf(today) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysOfWeek.forEachIndexed { i, day ->
            Day(
                day = day,
                // Simple example for numbers, can be improved to match the actual calendar
                number = (LocalDate.now().dayOfMonth - today + i).toString(),
                isSelected = i == selectedIndex,
                onClick = { selectedIndex = i }
            )
        }
    }
}

@Composable
fun Day(
    day: String,
    number: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val palette = MaterialTheme.colorScheme
    // Use onSurfaceVariant for better contrast in light/dark themes
    val textColor = if (isSelected) palette.primary else palette.onSurfaceVariant

    Column(
        modifier = Modifier
            .width(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = number,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 18.sp
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(color = palette.primary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeek() {
    myTodosTheme(dynamicColor = false) {
        Week()
    }
}
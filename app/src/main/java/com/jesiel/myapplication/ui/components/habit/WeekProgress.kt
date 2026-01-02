package com.jesiel.myapplication.ui.components.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.ui.components.toColor
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyProgressRow(habit: Habit) {
    val today = LocalDate.now()
    val habitColor = habit.color?.toColor() ?: MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val isCompleted = habit.completedDays.contains(date.toEpochDay())
            val dayInitial = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dayInitial,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (i == 0) habitColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) habitColor else Color.Transparent
                        )
                        .border(
                            width = 2.dp,
                            color = if (isCompleted) habitColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
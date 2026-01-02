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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.ui.components.toColor
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyProgressRow(habit: Habit) {
    val today = remember { LocalDate.now() }
    val habitColor = habit.color?.toColor() ?: MaterialTheme.colorScheme.primary
    
    val completedDaysSet = remember(habit.completedDays, habit.lastCompletedDay) { 
        val set = habit.completedDays.toMutableSet()
        // Fallback: se lastCompletedDay existir, garante que ele está no set
        habit.lastCompletedDay?.let { set.add(it) }
        set
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val days = remember(today) {
            (6 downTo 0).map { today.minusDays(it.toLong()) }
        }

        days.forEach { date ->
            val dateEpoch = date.toEpochDay()
            val isCompleted = completedDaysSet.contains(dateEpoch)
            val isToday = date.isEqual(today)
            val dayInitial = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dayInitial,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isToday) habitColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) habitColor.copy(alpha = 0.15f) else Color.Transparent
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isCompleted) habitColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Text(
                            text = "🔥",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

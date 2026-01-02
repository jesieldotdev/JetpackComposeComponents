package com.jesiel.myapplication.ui.components.form

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.viewmodel.ReminderReceiver
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ReminderSelector(
    context: Context,
    reminderTime: Long?,
    onReminderSelected: (Long?) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                showDateTimePicker(context, reminderTime, onReminderSelected)
            },
        color = if (reminderTime != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications, 
                contentDescription = null, 
                tint = if (reminderTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (reminderTime != null) {
                    val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderTime), ZoneId.systemDefault())
                    "Lembrete: ${ldt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}"
                } else {
                    "Adicionar um lembrete"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (reminderTime != null) FontWeight.Bold else FontWeight.Medium,
                color = if (reminderTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun showDateTimePicker(
    context: Context,
    currentTime: Long?,
    onReminderSelected: (Long?) -> Unit
) {
    val currentDateTime = Calendar.getInstance()
    if (currentTime != null) currentDateTime.timeInMillis = currentTime
    
    DatePickerDialog(context, { _, y, m, d ->
        TimePickerDialog(context, { _, hh, mm ->
            val cal = Calendar.getInstance().apply { set(y, m, d, hh, mm) }
            val newTime = cal.timeInMillis
            onReminderSelected(newTime)
            
            // confirmation notification
            val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(newTime), ZoneId.systemDefault())
            val formatted = ldt.format(DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm"))
            val testIntent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("task_title", "Lembrete definido para $formatted")
            }
            context.sendBroadcast(testIntent)
        }, currentDateTime.get(Calendar.HOUR_OF_DAY), currentDateTime.get(Calendar.MINUTE), true).show()
    }, currentDateTime.get(Calendar.YEAR), currentDateTime.get(Calendar.MONTH), currentDateTime.get(Calendar.DAY_OF_MONTH)).show()
}

package com.jesiel.myapplication.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jesiel.myapplication.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("task_id", -1)
        val title = intent.getStringExtra("task_title") ?: "Lembrete de Tarefa"
        val description = intent.getStringExtra("task_description")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "todo_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lembretes de Tarefas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                this.description = "Notificações de lembretes do myTodos"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent with DeepLink or Extras to open the specific task
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (taskId != -1) {
                // We use a custom action or data to help the UI identify the navigation target
                data = Uri.parse("mytodos://task/$taskId")
                putExtra("navigate_to_task_id", taskId)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            if (taskId != -1) taskId else 0, 
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description ?: "Lembrete: myTodos")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(if (description != null) NotificationCompat.BigTextStyle().bigText(description) else null)
            .build()

        notificationManager.notify(if (taskId != -1) taskId else System.currentTimeMillis().toInt(), notification)
    }
}

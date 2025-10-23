package com.jesiel.myapplication.ui.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

data class Todo(val text: String, val checked: Boolean)

class NewAppWidget : GlanceAppWidget() {

    private val todos = listOf(
        Todo("Comprar pão", false),
        Todo("Estudar Glance", true),
        Todo("Fazer café", false)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                TodoList(todos)
            }
        }
    }
}

@Composable
private fun TodoList(todos: List<Todo>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(16.dp)
    ) {
        Text(
            "Minhas Tarefas",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onSurface
            )
        )
        Spacer(modifier = GlanceModifier.height(16.dp))
        LazyColumn {
            items(todos) { todo ->
                TodoItem(todo = todo)
            }
        }
    }
}

@Composable
private fun TodoItem(todo: Todo) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckBox(
            checked = todo.checked,
            onCheckedChange = null // No action for now
        )
        Text(
            text = todo.text,
            modifier = GlanceModifier.padding(start = 8.dp),
            style = TextStyle(color = GlanceTheme.colors.onSurface)
        )
    }
}

class NewAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NewAppWidget()
}

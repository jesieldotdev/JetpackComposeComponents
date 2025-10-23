package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.ui.theme.myTodosTheme

@Composable
fun Card(task: Task, onToggleStatus: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Checkbox(
            checked = task.done,
            onCheckedChange = { onToggleStatus(task.id) }
        )
        Text(
            text = task.title,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    myTodosTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(
                task = Task(
                    id = 1,
                    title = "Wakeup",
                    done = false
                ),
                onToggleStatus = {}
            )
            Card(
                task = Task(
                    id = 2,
                    title = "Morning exercises",
                    done = true
                ),
                onToggleStatus = {}
            )
        }
    }
}
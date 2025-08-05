package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.screens.Task
import com.jesiel.myapplication.ui.theme.Grey60
import com.jesiel.myapplication.ui.theme.MyApplicationTheme

@Composable
fun Card(task: Task) {

    Column(
        modifier = Modifier
//            .shadow(
//                2.dp,
//                shape = RoundedCornerShape(2.dp)
//            )
            .clip(RoundedCornerShape(16.dp))

            .fillMaxWidth()
            .background(if (task.done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .padding(
                32.dp, 8.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.W800,
                fontSize = 20.sp,
                color = if (task.done) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = task.createdAt,
                color = if(task.done) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = task.text,
                color = if(task.done) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    MyApplicationTheme(dynamicColor = false) {
        Column {
            Card(
                Task(
                title = "Wakeup",
                text = "Early from bed and fresh",
                done = false,
                createdAt = "13:00"
            ))
            Spacer(modifier = Modifier.height(8.dp))
            Task(
                title = "Morning exercises",
                text = "4 types of exercise",
                done = true,
                createdAt = "16:00"
            )
        }
    }


}
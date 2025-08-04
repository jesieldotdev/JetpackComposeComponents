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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.theme.Blue40
import com.jesiel.myapplication.ui.theme.Grey60
import com.jesiel.myapplication.ui.theme.MyApplicationTheme

@Composable
fun Card() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(Color(0xFFF8F9FD))
            .padding(24.dp, 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Wakeup",
                fontWeight = FontWeight.W800,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "7:00 AM",
                color = Grey60
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Early wakeup from bed and fresh",
                color = Grey60
                )
        }
    }
}

@Preview
@Composable
fun CardPreview() {
    MyApplicationTheme {
        Card()
    }
}
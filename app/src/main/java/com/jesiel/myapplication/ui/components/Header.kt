package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.theme.MyApplicationTheme

@Composable
fun Header() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ago 5, 2025",
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            text = "Today",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        )
    }

}

@Preview
@Composable
fun HeaderPreview() {
    MyApplicationTheme {
        Header()
    }
}
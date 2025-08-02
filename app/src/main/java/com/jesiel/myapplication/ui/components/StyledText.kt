package com.jesiel.myapplication.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun StyledText(
    text: String
) {
    Text(
        text,
        fontWeight = FontWeight.Light,
        fontSize = 24.sp,
    )
}
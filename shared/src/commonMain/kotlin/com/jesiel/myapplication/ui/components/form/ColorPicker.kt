package com.jesiel.myapplication.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    colors: List<String>,
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Prioridade Visual (Cor)", 
            style = MaterialTheme.typography.labelLarge, 
            color = MaterialTheme.colorScheme.primary, 
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { hex ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(hexToColor(hex))
                        .border(
                            width = if (selectedColor == hex) 3.dp else 0.dp,
                            color = if (selectedColor == hex) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(hex) }
                )
            }
        }
    }
}

fun hexToColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return Color(cleanHex.toLong(16) or 0xFF000000)
}

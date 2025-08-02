package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomCheckbox(label: String){
    var checked by remember { mutableStateOf(false) }

    Checkbox(
        checked,
        onCheckedChange = {checked = it},
        modifier = Modifier.padding(0.dp)

    )
    Text(label)
}
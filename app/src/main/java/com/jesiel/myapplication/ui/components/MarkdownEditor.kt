package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun MarkdownEditor() {
    var text by remember { mutableStateOf("") }
    var isPreview by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Edit", modifier = Modifier.weight(1f))
            Switch(
                checked = isPreview,
                onCheckedChange = { isPreview = it }
            )
            Text("Preview", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (!isPreview) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Markdown input") },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
//                elevation = 2.dp
            ) {
                MarkdownText(
                    markdown = text,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
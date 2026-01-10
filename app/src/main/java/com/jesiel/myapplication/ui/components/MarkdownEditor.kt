package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.ui.theme.MyTodosTheme
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun MarkdownEditor() {
    var text by remember { mutableStateOf("") }
    var isPreview by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Edit",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            Switch(
                checked = isPreview,
                onCheckedChange = { isPreview = it }
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://www.svgrepo.com/show/334668/file-md.svg")
                    .decoderFactory(coil.decode.SvgDecoder.Factory())
                    .build(),
                contentDescription = "SVG Icon",
                modifier = Modifier.size(24.dp)
            )
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
                tonalElevation = 2.dp
            ) {
                MarkdownText(
                    markdown = text,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarkdownPreview(){
    MyTodosTheme {
        MarkdownEditor()
    }
}

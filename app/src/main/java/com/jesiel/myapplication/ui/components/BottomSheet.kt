package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleBottomSheet() {
    // Estado de controle
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    // Botão para mostrar o bottom modal
    Button(onClick = { showSheet = true }) {
        Text("Abrir Bottom Modal")
    }

    // O ModalBottomSheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            // Conteúdo do modal
            Text(
                text = "Conteúdo do Bottom ModalSheet!",
                modifier = Modifier.padding(24.dp)
            )
            Button(onClick = { showSheet = false }) {
                Text("Fechar")
            }
        }
    }
}
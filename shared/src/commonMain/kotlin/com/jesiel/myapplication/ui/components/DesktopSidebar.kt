package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DesktopSidebar(
    onNavigateToHabits: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onExit: () -> Unit = {}
//    onNavigateToDetail: (Int) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary



    Column(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        primaryColor,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {


        Item(
            onClick = onNavigateToHabits,
            icon = Icons.Default.CheckCircle,     // ✅ Agora usa =
            description = "Tarefas"                // ✅ Agora usa =
        )

        Item(
            onClick = onNavigateToHabits,
            icon = Icons.Default.Refresh,
            description = "Habitos"
        )

        Item(
            onClick = onNavigateToSettings,
            icon = Icons.Default.Settings,
            description = "Configurações"
        )






        Spacer(modifier = Modifier.weight(1f))

        Item(
            icon=Icons.AutoMirrored.Filled.ExitToApp,
            description = "Sair",
            onClick = onExit
            )
    }

}

@Composable
fun Item(
    onClick: (() -> Unit)? = null,
    icon:  ImageVector,
    description: String?,
    active: Boolean = false
) {
    IconButton(
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (active) {

                Color.White.copy(alpha = 0.7f)
            } else {
                Color.White
            },
            modifier = Modifier. size(28.dp)
        )
    }
}
package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    isKanbanMode: Boolean,
    onToggleKanban: () -> Unit,
    onNavigateToHome: () -> Unit, // Added missing parameter
    onNavigateToHabits: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        drawerShape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp),
        modifier = Modifier.width(300.dp)
    ) {
        DrawerHeader()
        
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp), 
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Navigation Options
        DrawerItem(
            label = "Tarefas",
            icon = Icons.Default.Done,
            onClick = onNavigateToHome
        )

        DrawerItem(
            label = "Hábitos",
            icon = Icons.Default.Refresh,
            onClick = onNavigateToHabits
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f)
        )

        DrawerItem(
            label = if (isKanbanMode) "Ver em Lista" else "Modo Kanban (Pro)",
            icon = if (isKanbanMode) Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Filled.ArrowForward,
            onClick = onToggleKanban
        )

        DrawerItem(
            label = "Configurações",
            icon = Icons.Default.Settings,
            onClick = onNavigateToSettings
        )
        
        DrawerItem(
            label = "Sobre",
            icon = Icons.Default.Info,
            onClick = onNavigateToAbout
        )
    }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Taska",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Gerencie sua rotina com elegância",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = label, fontWeight = FontWeight.Medium) },
        selected = false,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

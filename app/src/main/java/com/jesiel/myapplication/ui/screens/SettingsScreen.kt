package com.jesiel.myapplication.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jesiel.myapplication.viewmodel.AppFont
import com.jesiel.myapplication.viewmodel.AppTheme
import com.jesiel.myapplication.viewmodel.ThemeViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    todoViewModel: TodoViewModel,
    themeViewModel: ThemeViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by todoViewModel.uiState.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }

    val themeLabel = when (themeState.theme) {
        AppTheme.LIGHT -> "Claro"
        AppTheme.DARK -> "Escuro"
        AppTheme.SYSTEM -> "Padrão do sistema"
    }

    val fontLabel = when (themeState.font) {
        AppFont.SYSTEM -> "Sistema"
        AppFont.POPPINS -> "Poppins"
        AppFont.MONOSPACE -> "Monospace"
        AppFont.SERIF -> "Serif"
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Escolha o Tema") },
            text = {
                Column {
                    listOf(
                        "Claro" to AppTheme.LIGHT,
                        "Escuro" to AppTheme.DARK,
                        "Padrão do sistema" to AppTheme.SYSTEM
                    ).forEach { (label, theme) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    themeViewModel.setTheme(theme)
                                    showThemeDialog = false 
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (theme == themeState.theme), onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Fechar") }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text("Escolha a Fonte") },
            text = {
                Column {
                    listOf(
                        "Sistema" to AppFont.SYSTEM,
                        "Poppins" to AppFont.POPPINS,
                        "Monospace" to AppFont.MONOSPACE,
                        "Serif" to AppFont.SERIF
                    ).forEach { (label, font) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    themeViewModel.setFont(font)
                                    showFontDialog = false 
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (font == themeState.font), onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontDialog = false }) { Text("Fechar") }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showBackgroundImage) {
            AsyncImage(
                model = uiState.backgroundImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(uiState.blurIntensity.dp),
                contentScale = ContentScale.Crop
            )
        }
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background.copy(alpha = if (uiState.showBackgroundImage) 0.6f else 1f)))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Configurações", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsSection(title = "Assinatura") {
                    SettingsClickableItem(
                        title = "Versão Pro",
                        subtitle = if (themeState.isUserPro) "Você já é um usuário Pro! Aproveite o Kanban." else "Desbloqueie o Modo Kanban e mais",
                        icon = Icons.Default.Star,
                        trailing = {
                            Switch(
                                checked = themeState.isUserPro,
                                onCheckedChange = { themeViewModel.setUserPro(it) }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SettingsSection(title = "Aparência") {
                    SettingsClickableItem(
                        title = "Cores Dinâmicas",
                        subtitle = "Usar cores do papel de parede",
                        icon = Icons.Default.Settings,
                        trailing = {
                            Switch(
                                checked = themeState.useDynamicColors,
                                onCheckedChange = { themeViewModel.setDynamicColors(it) }
                            )
                        }
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                    
                    SettingsClickableItem(
                        title = "Tema do App",
                        subtitle = themeLabel,
                        icon = Icons.Default.Create,
                        onClick = { showThemeDialog = true }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )

                    SettingsClickableItem(
                        title = "Tipografia",
                        subtitle = fontLabel,
                        icon = Icons.Default.Face,
                        onClick = { showFontDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SettingsSection(title = "Personalização de Fundo") {
                    SettingsClickableItem(
                        title = "Exibir Imagem",
                        subtitle = "Ativar ou desativar papel de parede",
                        icon = Icons.Default.Settings,
                        trailing = {
                            Switch(
                                checked = uiState.showBackgroundImage,
                                onCheckedChange = { todoViewModel.setShowBackgroundImage(it) }
                            )
                        }
                    )
                    
                    if (uiState.showBackgroundImage) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                        
                        SettingsClickableItem(
                            title = "Trocar Imagem",
                            subtitle = "Sorteia um novo papel de parede",
                            icon = Icons.Default.Refresh,
                            onClick = { todoViewModel.refreshBackgroundImage() }
                        )
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                        
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(16.dp))
                                Text("Intensidade do Blur", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Slider(
                                value = uiState.blurIntensity,
                                onValueChange = { todoViewModel.updateBlurIntensity(it) },
                                valueRange = 0f..50f,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SettingsSection(title = "Sistema") {
                    SettingsClickableItem(
                        title = "Notificações",
                        subtitle = "Gerenciar alertas e lembretes",
                        icon = Icons.Default.Notifications,
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
            tonalElevation = 2.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        if (trailing != null) {
            trailing()
        }
    }
}

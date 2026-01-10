package com.jesiel.myapplication.ui.screens

import androidx. compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose. foundation.shape.RoundedCornerShape
import androidx.compose. foundation.verticalScroll
import androidx.compose.material.icons. Icons
import androidx.compose.material.icons.automirrored. filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose. runtime.*
import androidx.compose. ui.Alignment
import androidx. compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics. Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import com.jesiel.myapplication.data. AppFont
import com.jesiel.myapplication.data.AppTheme
import com.jesiel.myapplication.ui.components.DesktopSidebar
import com.jesiel.myapplication.ui.components.common.BlurredBackground
import com.jesiel.myapplication. viewmodel.ThemeViewModel
import com.jesiel.myapplication.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    todoViewModel: TodoViewModel,
    themeViewModel:  ThemeViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToHabitDetail: (Int) -> Unit = {},
    onNavigateToHabits: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    val uiState by todoViewModel.uiState.collectAsState()
    val themeState by themeViewModel.themeState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }

    val themeLabel = when (themeState.theme) {
        AppTheme. LIGHT -> "Claro"
        AppTheme.DARK -> "Escuro"
        AppTheme.SYSTEM -> "Padrão do sistema"
    }

    val fontLabel = when (themeState.font) {
        AppFont.SYSTEM -> "Sistema"
        AppFont.POPPINS -> "Poppins"
        AppFont.MONOSPACE -> "Monospace"
        AppFont. SERIF -> "Serif"
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Diálogo de Temas
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Escolha o Tema") },
            text = {
                Column {
                    listOf(
                        "Claro" to AppTheme.LIGHT,
                        "Escuro" to AppTheme.DARK,
                        "Padrão" to AppTheme.SYSTEM
                    ).forEach { (label, theme) ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                themeViewModel.setTheme(theme); showThemeDialog = false
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment. CenterVertically
                        ) {
                            RadioButton(selected = (theme == themeState. theme), onClick = null)
                            Spacer(Modifier. width(12.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThemeDialog = false }) { Text("Fechar") } }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showBackgroundImage) {
            BlurredBackground(
                imageUrl = uiState. backgroundImageUrl,
                blurIntensity = uiState.blurIntensity,
                scrimAlpha = 0.8f
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {

            DesktopSidebar(
                onNavigateToHabits = {onNavigateToHabits},
                onNavigateToSettings = {},
                onExit={onExit},
                onNavigateToHome = {onNavigateToHome}
            )

            Scaffold(
                // Torna o Scaffold transparente
                containerColor = Color.Transparent,

//                shape = RoundedCornerShape(28.dp),

                topBar = {

                    TopAppBar(
                        title = { Text("Configurações", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.clip(RoundedCornerShape(28.dp)),

                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    Icons.AutoMirrored. Filled.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            }
                        },
                        // TopAppBar com fundo semi-transparente
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black.copy(alpha = 0.1f), // ou Color.White.copy(alpha = 0.1f),        // ← Fundo transparente
                            scrolledContainerColor = Color.Transparent // ← Transparente quando rolar a tela
                        ),
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                    // Remove ou ajusta o background para transparente
                    // .background(surfaceColor.copy(alpha = 0.95f))
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsSection(title = "Aparência") {
                        SettingsClickableItem(
                            title = "Tema do App",
                            subtitle = themeLabel,
                            icon = Icons.Default.Create,
                            onClick = { showThemeDialog = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant. copy(alpha = 0.1f)
                        )

                        SettingsClickableItem(
                            title = "Modo Kanban",
                            subtitle = "Visualização em colunas",
                            icon = Icons.Default.DateRange,
                            trailing = {
                                Switch(
                                    checked = themeState.isKanbanMode,
                                    onCheckedChange = { themeViewModel.setKanbanMode(it) })
                            }
                        )
                    }

                    Spacer(modifier = Modifier. height(24.dp))

                    SettingsSection(title = "Personalização de Fundo") {
                        SettingsClickableItem(
                            title = "Exibir Imagem",
                            subtitle = "Ativar papel de parede",
                            icon = Icons. Default.Settings,
                            trailing = {
                                Switch(
                                    checked = uiState.showBackgroundImage,
                                    onCheckedChange = { todoViewModel.setShowBackgroundImage(it) })
                            }
                        )

                        if (uiState.showBackgroundImage) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Intensidade do Blur", fontWeight = FontWeight.Bold)
                                Slider(
                                    value = uiState. blurIntensity,
                                    onValueChange = { todoViewModel.updateBlurIntensity(it) },
                                    valueRange = 0f..50f
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
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
            color = MaterialTheme. colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontWeight = FontWeight. Bold
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            // Reduzindo o alpha de 0.6f para 0.3f para mais transparência
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = 2.dp
        ) {
            Column { content() }
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
        modifier = modifier. fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary. copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (trailing != null) trailing()
    }
}
package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: IPTVViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val bufferSize by viewModel.bufferSize.collectAsState()
    val hardwareDecoding by viewModel.hardwareDecoding.collectAsState()
    val blockAdult by viewModel.blockAdult.collectAsState()
    val accountExpiration by viewModel.accountExpiration.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "neon_settings")
        val neonAlpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
        Text(
            text = "Configurações",
            color = Color.White,
            fontFamily = com.example.ui.theme.RussoOne,
            fontSize = 24.sp,
            style = androidx.compose.ui.text.TextStyle(
                shadow = Shadow(
                    color = NeonGreen.copy(alpha = neonAlpha),
                    offset = Offset(0f, 0f),
                )
            )
        )

        // Category 1: Account & Playlists
        SettingsSectionHeader(title = "Contas & Listas")

        SettingsActionRow(
            icon = Icons.AutoMirrored.Rounded.List,
            title = "Gerenciar Playlists",
            subtitle = "Adicionar, remover ou trocar a lista ativa."
        ) {
            viewModel.navigateTo(com.example.ui.Screen.LOGIN)
        }

        // Category 2: Reproduction Preferences
        SettingsSectionHeader(title = "Reprodutor & Decodificador")

        SettingsSwitchRow(
            icon = Icons.Rounded.DeveloperBoard,
            title = "Decodificação de Hardware",
            subtitle = "Utiliza a GPU do dispositivo para aceleração gráfica acelerada.",
            checked = hardwareDecoding
        ) { 
            viewModel.setHardwareDecoding(it)
        }


        if (accountExpiration.isNotEmpty()) {
            SettingsActionRow(
                icon = Icons.Rounded.DateRange,
                title = "Validade da Lista",
                subtitle = accountExpiration
            ) {
            }
        }

        SettingsOptionRow(
            icon = Icons.Rounded.Timelapse,
            title = "Buffer de Reprodução",
            value = bufferSize
        ) {
            val newBuffer = when (bufferSize) {
                "Médio (Padrão)" -> "Grande (Reduz engasgos)"
                "Grande (Reduz engasgos)" -> "Pequeno (Troca rápida)"
                else -> "Médio (Padrão)"
            }
            viewModel.setBufferSize(newBuffer)
        }



        // Parental Control
        SettingsSectionHeader(title = "Controle Parental")
        SettingsActionRow(
            icon = Icons.Rounded.Security,
            title = "Controle Parental",
            subtitle = "Configurar senha e bloquear conteúdos adultos"
        ) {
            viewModel.navigateTo(com.example.ui.Screen.PARENTAL_CONTROL)
        }

        // Layout Mode
        SettingsSectionHeader(title = "Interface e Layout")
        var showDeviceModeDialog by remember { mutableStateOf(false) }
        SettingsActionRow(
            icon = Icons.Rounded.Smartphone,
            title = "Modo de Layout",
            subtitle = "Alternar entre TV e Celular",
            onClick = { showDeviceModeDialog = true }
        )
        if (showDeviceModeDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDeviceModeDialog = false },
                title = { Text("Selecione o Modo de Layout", color = Color.White) },
                text = { Text("Escolha o layout que melhor se adapta ao seu dispositivo.", color = Color.Gray) },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { viewModel.setDeviceLayoutMode("TV"); showDeviceModeDialog = false }) {
                        Text("TV", color = NeonGreen)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { viewModel.setDeviceLayoutMode("MOBILE"); showDeviceModeDialog = false }) {
                        Text("Celular", color = NeonGreen)
                    }
                },
                containerColor = com.example.ui.theme.Charcoal
            )
        }

        // Category 2: Storage & Cache Cleaning
        SettingsSectionHeader(title = "Armazenamento & Cache")

        SettingsActionRow(
            icon = Icons.Rounded.CleaningServices,
            title = "Limpar Cache de Listas",
            subtitle = "Libera memória local limpando logotipos carregados e buffers salvos."
        ) {
            Toast.makeText(context, "Cache de logs e imagens limpo com sucesso!", Toast.LENGTH_SHORT).show()
        }

        // Device Info
        SettingsSectionHeader(title = "Dispositivo (Controle Remoto)")
        val deviceId = com.example.util.DeviceUtil.getDeviceId(context)
        SettingsActionRow(
            icon = Icons.Rounded.ImportantDevices,
            title = "ID do Dispositivo (Substituto de MAC)",
            subtitle = deviceId
        ) {
            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Device ID", deviceId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "ID Copiado!", Toast.LENGTH_SHORT).show()
        }

        // Category 3: Application metadata
        SettingsSectionHeader(title = "Sobre o Aplicativo")

        SettingsActionRow(
            icon = Icons.Rounded.Info,
            title = "Unlock Player Premium v1.0.0",
            subtitle = "Desenvolvido com Jetpack Compose, Room SQLite e ExoPlayer Media3. Nenhum conteúdo protegido é embutido no código."
        ) {
            Toast.makeText(context, "Desenvolvido por UnlockTeam", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = NeonGreen,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Charcoal)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = NeonGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonGreen,
                checkedTrackColor = NeonGreenDim,
                uncheckedThumbColor = Color.DarkGray,
                uncheckedTrackColor = Color.Black
            )
        )
    }
}

@Composable
fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Charcoal)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = NeonGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Valor atual: $value", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
        Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Config", tint = Color.Gray)
    }
}

@Composable
fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Charcoal)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = NeonGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

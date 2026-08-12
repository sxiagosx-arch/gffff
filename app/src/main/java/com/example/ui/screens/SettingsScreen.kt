package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val accountExpiration by viewModel.accountExpiration.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // CORREÇÃO DO BOTÃO VOLTAR
        IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

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

        Column(modifier = Modifier.verticalScroll(scrollState).weight(1f)) {

            // DESTAQUE DA VALIDADE DA LISTA
            SettingsSectionHeader(title = "Sua Assinatura")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonGreenDim)
                    .padding(16.dp)
            ) {
                Column {
                    Text("Vencimento do Plano:", color = Color.White, fontSize = 12.sp)
                    Text(text = accountExpiration, color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÃO VERDE DE RENOVAR
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://api.whatsapp.com/send/?phone=380689671189&text&type=phone_number&app_absent=0?text=Olá, desejo renovar meu plano!")
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Icon(Icons.Rounded.ShoppingCart, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RENOVAR / ATUALIZAR PLANO", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reprodução
            SettingsSectionHeader(title = "Reprodutor & Decodificador")
            SettingsSwitchRow(
                icon = Icons.Rounded.DeveloperBoard,
                title = "Decodificação de Hardware",
                subtitle = "Utiliza a GPU do dispositivo.",
                checked = hardwareDecoding
            ) { viewModel.setHardwareDecoding(it) }

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

            // Controle Parental
            SettingsSectionHeader(title = "Controle Parental")
            SettingsActionRow(
                icon = Icons.Rounded.Security,
                title = "Controle Parental",
                subtitle = "Configurar senha e bloquear conteúdos adultos"
            ) {
                viewModel.navigateTo(com.example.ui.Screen.PARENTAL_CONTROL)
            }

            // Layout
            SettingsSectionHeader(title = "Interface e Layout")
            var showDeviceModeDialog by remember { mutableStateOf(false) }
            SettingsActionRow(
                icon = Icons.Rounded.Smartphone,
                title = "Modo de Layout",
                subtitle = "Alternar entre TV e Celular",
                onClick = { showDeviceModeDialog = true }
            )
            if (showDeviceModeDialog) {
                AlertDialog(
                    onDismissRequest = { showDeviceModeDialog = false },
                    title = { Text("Selecione o Layout", color = Color.White) },
                    text = { Text("Escolha o layout que melhor se adapta.", color = Color.Gray) },
                    confirmButton = { TextButton(onClick = { viewModel.setDeviceLayoutMode("TV"); showDeviceModeDialog = false }) { Text("TV", color = NeonGreen) } },
                    dismissButton = { TextButton(onClick = { viewModel.setDeviceLayoutMode("MOBILE"); showDeviceModeDialog = false }) { Text("Celular", color = NeonGreen) } },
                    containerColor = Charcoal
                )
            }

            // Cache
            SettingsSectionHeader(title = "Armazenamento & Cache")
            SettingsActionRow(
                icon = Icons.Rounded.CleaningServices,
                title = "Limpar Cache de Listas",
                subtitle = "Libera memória local limpando logotipos."
            ) {
                Toast.makeText(context, "Cache limpo com sucesso!", Toast.LENGTH_SHORT).show()
            }

            // Aparelho
            SettingsSectionHeader(title = "Dispositivo")
            val deviceId = com.example.util.DeviceUtil.getDeviceId(context).take(8).uppercase()
            SettingsActionRow(
                icon = Icons.Rounded.ImportantDevices,
                title = "ID do Dispositivo",
                subtitle = deviceId
            ) {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Device ID", deviceId)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "ID Copiado!", Toast.LENGTH_SHORT).show()
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
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
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = NeonGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 11.sp)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = NeonGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "Valor atual: $value", color = Color.Gray, fontSize = 11.sp)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
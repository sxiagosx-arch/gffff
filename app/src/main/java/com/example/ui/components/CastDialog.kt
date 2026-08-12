package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastDialog(onDismiss: () -> Unit) {
    var isSearching by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000) // fake search time
        isSearching = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSearching) Icons.Rounded.Cast else Icons.Rounded.CastConnected,
                    contentDescription = null,
                    tint = if (isSearching) Color.White else NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isSearching) "Procurando dispositivos..." else "Transmitir para",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (isSearching) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nenhum dispositivo Chromecast ou compatível encontrado na sua rede Wi-Fi.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val localContext = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = {
                            try {
                                val intent = android.content.Intent("android.settings.CAST_SETTINGS")
                                localContext.startActivity(intent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(localContext, "Espelhamento nativo não suportado neste dispositivo.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Charcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tentar via Configurações do Sistema", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = NeonGreen)
            }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.Gray
    )
}

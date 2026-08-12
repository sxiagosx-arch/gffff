package com.example.ui.screens

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

import com.example.database.PlaylistAccount
import com.example.ui.IPTVUiState
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: IPTVViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Gera o ID do aparelho da mesma forma que estava no código original
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_ID"
    val deviceId = androidId.uppercase().take(8)

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Mostra botão de voltar caso ele já tenha uma conta salva no banco local
        if (accounts.any { it.isActive }) {
            IconButton(
                onClick = { viewModel.navigateTo(com.example.ui.Screen.HOME) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(36.dp))
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                val infiniteTransition = rememberInfiniteTransition(label = "neon")
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
                    text = "Unlock Player",
                    color = Color.White,
                    fontFamily = com.example.ui.theme.RussoOne,
                    fontSize = 24.sp,
                    letterSpacing = 2.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = NeonGreen.copy(alpha = neonAlpha),
                            offset = Offset(0f, 0f),
                        )
                    )
                )
                Text(
                    text = "Acesse suas listas de reprodução",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Charcoal)
                        .padding(24.dp)
                ) {
                    Text("Para ativar sua TV, envie o ID abaixo para o suporte:", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = deviceId,
                            color = NeonGreen,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Após o suporte liberar, clique em 'VERIFICAR ATIVAÇÃO'", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = ""

                            coroutineScope.launch {
                                try {
                                    // ⚠️ PROGRAMADOR: MUDE A URL ABAIXO PARA O SEU SITE REAL
                                    val apiUrl = "https://lojavip.net/api/verificar_mac/$deviceId"

                                    val response = withContext(Dispatchers.IO) {
                                        val url = URL(apiUrl)
                                        val connection = url.openConnection() as HttpURLConnection
                                        connection.requestMethod = "GET"
                                        connection.connectTimeout = 5000

                                        if (connection.responseCode == 200) {
                                            connection.inputStream.bufferedReader().use { it.readText() }
                                        } else {
                                            connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                                        }
                                    }

                                    val json = JSONObject(response)

                                    if (json.optString("status") == "sucesso") {
                                        val tipo = json.optString("tipo")
                                        val url = json.optString("url")

                                        // Usa o mesmo ViewModel original para salvar a lista no Room Database do App
                                        if (tipo == "xtream") {
                                            viewModel.addAccount(
                                                PlaylistAccount(
                                                    name = "Minha TV",
                                                    type = "XTREAM",
                                                    serverUrl = url,
                                                    username = json.optString("user"),
                                                    password = json.optString("pass")
                                                )
                                            )
                                        } else {
                                            viewModel.addAccount(
                                                PlaylistAccount(
                                                    name = "Minha TV",
                                                    type = "M3U_URL",
                                                    m3uUrl = url
                                                )
                                            )
                                        }
                                    } else {
                                        errorMessage = json.optString("msg", "Aparelho não ativado. Fale com o suporte.")
                                    }

                                } catch (e: Exception) {
                                    errorMessage = "Erro de conexão. Verifique sua internet."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading || uiState is IPTVUiState.Loading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "VERIFICAR ATIVAÇÃO",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }

        // Error Dialog do ViewModel (mantido da versão original para tratar erros de M3U)
        if (uiState is IPTVUiState.Error) {
            val errMsg = (uiState as IPTVUiState.Error).message
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = NeonGreen)
                    }
                },
                title = { Text("Aviso", color = Color.White) },
                text = { Text(errMsg, color = Color.LightGray) },
                containerColor = Charcoal
            )
        }
    }
}

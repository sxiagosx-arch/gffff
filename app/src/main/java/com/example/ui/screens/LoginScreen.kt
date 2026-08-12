package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import com.example.database.PlaylistAccount
import com.example.ui.IPTVUiState
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.GraySurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: IPTVViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Xtream Codes, 1 = M3U URL, 2 = M3U File
    
    // Xtream states
    var listName by remember { mutableStateOf("") }
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // M3U state
    var m3uUrl by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .testTag("login_screen_container")
    ) {
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
                // Small glow logo
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
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tab selectors
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Charcoal)
                        .padding(4.dp)
                ) {
                    TabItem(
                        title = "Xtream API",
                        selected = selectedTab == 0,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 0 }
                    TabItem(
                        title = "URL M3U",
                        selected = selectedTab == 1,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 1 }
                    TabItem(
                        title = "Ativação ID",
                        selected = selectedTab == 2,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 2 }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Input Fields
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Charcoal)
                        .padding(20.dp)
                ) {
                    if (selectedTab != 2) {
                        Text(
                            text = "Configurar Lista",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = listName,
                            onValueChange = { listName = it },
                            label = { Text("Nome da Lista (ex: Minha TV)", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }

                    if (selectedTab == 0) {
                        OutlinedTextField(
                            value = serverUrl,
                            onValueChange = { serverUrl = it },
                            label = { Text("URL do Servidor (ex: http://ex.com:80)", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Usuário", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Senha", color = Color.Gray) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (selectedTab == 1) {
                        OutlinedTextField(
                            value = m3uUrl,
                            onValueChange = { m3uUrl = it },
                            label = { Text("Link M3U / M3U8", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val androidId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "UNKNOWN_ID"
                        val deviceId = androidId.uppercase().take(8)
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("Para ativar sua TV, envie o ID abaixo para o suporte:", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black).padding(horizontal = 24.dp, vertical = 12.dp)) {
                                Text(deviceId, color = NeonGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("WhatsApp Suporte: +55 11 99999-9999", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Após o suporte liberar, clique em 'VERIFICAR ATIVAÇÃO'", color = Color.LightGray, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (selectedTab == 2) {
                                // Aqui vamos conectar com a API futuramente
                                viewModel.showError("Funcionalidade de ID requer integração com backend. O admin precisa criar a rota no site para liberar.")
                            } else if (listName.isNotEmpty()) {
                                if (selectedTab == 0 && serverUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                                    viewModel.addAccount(
                                        PlaylistAccount(
                                            name = listName,
                                            type = "XTREAM",
                                            serverUrl = serverUrl,
                                            username = username,
                                            password = password
                                        )
                                    )
                                } else if (selectedTab == 1 && m3uUrl.isNotEmpty()) {
                                    val xtreamRegex = "(https?://[^/]+)/get\\.php.*username=([^&]+).*password=([^&]+)".toRegex(RegexOption.IGNORE_CASE)
                                    val match = xtreamRegex.find(m3uUrl)
                                    if (match != null && match.groupValues.size >= 4) {
                                        viewModel.addAccount(
                                            PlaylistAccount(
                                                name = listName,
                                                type = "XTREAM",
                                                serverUrl = match.groupValues[1],
                                                username = match.groupValues[2],
                                                password = match.groupValues[3]
                                            )
                                        )
                                    } else {
                                        viewModel.addAccount(
                                            PlaylistAccount(
                                                name = listName,
                                                type = "M3U_URL",
                                                m3uUrl = m3uUrl
                                            )
                                        )
                                    }
                                }
                            } else {
                                viewModel.showError("Por favor, preencha o Nome da Lista.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_login_button")
                    ) {
                        Text(
                            text = if (selectedTab == 2) "VERIFICAR ATIVAÇÃO" else "SALVAR E CONECTAR",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Existing Profiles (Contas salvas)
            if (accounts.isNotEmpty()) {
                item {
                    Text(
                        text = "Contas Salvas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Start
                    )
                }

                items(accounts) { acc ->
                    SavedAccountCard(
                        account = acc,
                        onSelect = { viewModel.selectAccount(acc.id) },
                        onDelete = { viewModel.deleteAccount(acc.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }


        }

        // Loading HUD
        if (uiState is IPTVUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeonGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Processando Lista...", color = NeonGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Error Dialog
        if (uiState is IPTVUiState.Error) {
            val errMsg = (uiState as IPTVUiState.Error).message
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = NeonGreen)
                    }
                },
                title = { Text("Erro", color = Color.White) },
                text = { Text(errMsg, color = Color.LightGray) },
                containerColor = Charcoal
            )
        }
    }
}

@Composable
fun TabItem(title: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) NeonGreen else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.Black else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SavedAccountCard(account: PlaylistAccount, onSelect: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Charcoal)
            .clickable { onSelect() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NeonGreenDim),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (account.type == "XTREAM") Icons.Rounded.Dns else Icons.Rounded.Link,
                    contentDescription = "Tipo",
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = account.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = if (account.type == "XTREAM") account.serverUrl else "M3U Link",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Deletar", tint = Color.Red.copy(alpha = 0.8f))
        }
    }
}

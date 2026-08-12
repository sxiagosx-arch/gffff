with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

import re

# Find the start of the Input Fields section
start_idx = content.find('            // Input Fields')
end_idx = content.find('            // Existing Profiles (Contas salvas)')

old_section = content[start_idx:end_idx]

new_section = """            // Input Fields
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
                                viewModel.setError("Funcionalidade de ID requer integração com backend. O admin precisa criar a rota no site para liberar.")
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
                                    val xtreamRegex = "(https?://[^/]+)/get\\\\.php.*username=([^&]+).*password=([^&]+)".toRegex(RegexOption.IGNORE_CASE)
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
                                viewModel.setError("Por favor, preencha o Nome da Lista.")
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

"""

content = content.replace(old_section, new_section)

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)

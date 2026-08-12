with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

import re

old_inputs = """                    } else {
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
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button("""

new_inputs = """                    } else if (selectedTab == 1) {
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
                            Text("Após o suporte liberar, clique em 'Verificar Ativação'", color = Color.LightGray, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button("""

content = content.replace(old_inputs, new_inputs)

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)

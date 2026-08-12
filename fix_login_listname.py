with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

import re

old_config_title = """                    Text(
                        text = "Configurar Lista",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (selectedTab == 0) {"""

new_config_title = """                    Text(
                        text = "Configurar Lista",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (selectedTab != 2) {
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
                    if (selectedTab == 0) {"""

content = content.replace(old_config_title, new_config_title)

# Also fix the button logic properly
old_btn_logic = """                        onClick = {
                            if (selectedTab == 2) {
                                // Aqui você conectaria com sua API (Site ou Bot do Telegram)
                                // Exemplo: fetch("https://seusite.com/api/get_playlist?id=$deviceId")
                                viewModel.setError("Funcionalidade de ID requer integração com backend. Altere a URL da API no código.")
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
                            }
                        },"""

new_btn_logic = """                        onClick = {
                            if (selectedTab == 2) {
                                viewModel.setError("Funcionalidade de ID requer integração com backend. Você pode conectar com seu Site ou Bot do Telegram usando esta tela.")
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
                        },"""

content = content.replace(old_btn_logic, new_btn_logic)

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)

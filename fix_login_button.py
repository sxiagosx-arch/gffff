with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

import re

old_btn_logic = """                        onClick = {
                            if (listName.isNotEmpty()) {
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
                                    // Auto-detect Xtream Codes from M3U URL
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
                            }
                        },"""

new_btn_logic = """                        onClick = {
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

content = content.replace(old_btn_logic, new_btn_logic)

# Fix button text dynamically
content = content.replace('text = "SALVAR E CONECTAR"', 'text = if (selectedTab == 2) "VERIFICAR ATIVAÇÃO" else "SALVAR E CONECTAR"')

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)

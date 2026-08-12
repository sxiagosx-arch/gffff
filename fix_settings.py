with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

import re

old_account = """        // Category 1: Account & Playlists
        SettingsSectionHeader(title = "Contas & Listas")
        SettingsActionRow(
            icon = Icons.Rounded.List,
            title = "Gerenciar Playlists",
            subtitle = "Adicionar, remover ou trocar a lista ativa."
        ) {
            viewModel.navigateTo(com.example.ui.Screen.LOGIN)
        }"""

new_account = """        // Category 1: Account & Playlists
        SettingsSectionHeader(title = "Contas & Listas")
        val activeAccount = accounts.find { it.isActive }
        val accountName = activeAccount?.name ?: "Nenhuma lista ativa"
        val subtitle = if (accountExpiration.isNotEmpty()) "Dias restantes: $accountExpiration" else "Gerenciar suas playlists."
        SettingsActionRow(
            icon = Icons.AutoMirrored.Rounded.List,
            title = accountName,
            subtitle = subtitle
        ) {
            viewModel.navigateTo(com.example.ui.Screen.LOGIN)
        }"""

content = content.replace(old_account, new_account)
content = content.replace('Icons.Rounded.List', 'Icons.AutoMirrored.Rounded.List')

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

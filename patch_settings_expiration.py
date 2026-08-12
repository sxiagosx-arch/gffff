import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = """    val blockAdult by viewModel.blockAdult.collectAsState()"""
replace = """    val blockAdult by viewModel.blockAdult.collectAsState()
    val accountExpiration by viewModel.accountExpiration.collectAsState()"""

content = content.replace(target, replace)

target2 = """        // Category 1: Account & Playlists
        SettingsSectionHeader(title = "Contas & Listas")
        SettingsActionRow(
            icon = Icons.Rounded.List,
            title = "Gerenciar Playlists",
            subtitle = "Adicionar, remover ou trocar a lista ativa."
        ) {
            viewModel.navigateTo(com.example.ui.Screen.LOGIN)
        }"""
replace2 = """        // Category 1: Account & Playlists
        SettingsSectionHeader(title = "Contas & Listas")
        SettingsActionRow(
            icon = Icons.AutoMirrored.Rounded.List,
            title = "Gerenciar Playlists",
            subtitle = "Adicionar, remover ou trocar a lista ativa."
        ) {
            viewModel.navigateTo(com.example.ui.Screen.LOGIN)
        }
        SettingsActionRow(
            icon = Icons.Rounded.Event,
            title = "Validade da Conta Ativa",
            subtitle = accountExpiration,
            onClick = {}
        )"""

content = content.replace(target2, replace2)

if "import androidx.compose.material.icons.rounded.Event" not in content:
    content = content.replace("import androidx.compose.material.icons.rounded.*", "import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.rounded.Event")

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

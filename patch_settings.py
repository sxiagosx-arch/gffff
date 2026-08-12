import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = """        // Category 2: Storage & Cache Cleaning"""
replace = """        // Parental Control
        SettingsSectionHeader(title = "Controle Parental")
        SettingsActionRow(
            icon = Icons.Rounded.Security,
            title = "Controle Parental",
            subtitle = "Configurar senha e bloquear conteúdos adultos"
        ) {
            viewModel.navigateTo(com.example.ui.Screen.PARENTAL_CONTROL)
        }

        // Layout Mode
        SettingsSectionHeader(title = "Interface e Layout")
        var showDeviceModeDialog by remember { mutableStateOf(false) }
        SettingsActionRow(
            icon = Icons.Rounded.Smartphone,
            title = "Modo de Layout",
            subtitle = "Alternar entre TV e Celular",
            onClick = { showDeviceModeDialog = true }
        )
        if (showDeviceModeDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDeviceModeDialog = false },
                title = { Text("Selecione o Modo de Layout", color = Color.White) },
                text = { Text("Escolha o layout que melhor se adapta ao seu dispositivo.", color = Color.Gray) },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { viewModel.setDeviceLayoutMode("TV"); showDeviceModeDialog = false }) {
                        Text("TV", color = NeonGreen)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { viewModel.setDeviceLayoutMode("MOBILE"); showDeviceModeDialog = false }) {
                        Text("Celular", color = NeonGreen)
                    }
                },
                containerColor = com.example.ui.theme.Charcoal
            )
        }

        // Category 2: Storage & Cache Cleaning"""

content = content.replace(target, replace)

if "import androidx.compose.material.icons.rounded.Smartphone" not in content:
    content = content.replace("import androidx.compose.material.icons.rounded.*", "import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.rounded.Smartphone\nimport androidx.compose.material.icons.rounded.Security")

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

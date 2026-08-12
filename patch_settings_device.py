with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = """        // Category 2: Playback Settings
        SettingsSectionHeader(title = "Reprodução & Player")"""
replace = """        // Category 2: Playback Settings
        SettingsSectionHeader(title = "Reprodução & Player")
        
        var showDeviceModeDialog by remember { mutableStateOf(false) }
        SettingsActionRow(
            icon = androidx.compose.material.icons.rounded.Smartphone,
            title = "Modo de Layout",
            subtitle = "Alternar entre TV e Celular",
            onClick = { showDeviceModeDialog = true }
        )
        if (showDeviceModeDialog) {
            AlertDialog(
                onDismissRequest = { showDeviceModeDialog = false },
                title = { Text("Selecione o Modo de Layout", color = Color.White) },
                text = { Text("Escolha o layout que melhor se adapta ao seu dispositivo.", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = { viewModel.setDeviceLayoutMode("TV"); showDeviceModeDialog = false }) {
                        Text("TV", color = NeonGreen)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setDeviceLayoutMode("MOBILE"); showDeviceModeDialog = false }) {
                        Text("Celular", color = NeonGreen)
                    }
                },
                containerColor = Charcoal
            )
        }
"""
content = content.replace(target, replace)

if "import androidx.compose.material.icons.rounded.Smartphone" not in content:
    content = content.replace("import androidx.compose.material.icons.rounded.*", "import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.rounded.Smartphone")

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

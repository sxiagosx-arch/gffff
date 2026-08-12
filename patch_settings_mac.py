import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = """        // Category 3: Application metadata
        SettingsSectionHeader(title = "Sobre o Aplicativo")"""

replace = """        // Device Info
        SettingsSectionHeader(title = "Dispositivo (Controle Remoto)")
        val deviceId = com.example.util.DeviceUtil.getDeviceId(context)
        SettingsActionRow(
            icon = Icons.Rounded.ImportantDevices,
            title = "ID do Dispositivo (Substituto de MAC)",
            subtitle = deviceId
        ) {
            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Device ID", deviceId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "ID Copiado!", Toast.LENGTH_SHORT).show()
        }

        // Category 3: Application metadata
        SettingsSectionHeader(title = "Sobre o Aplicativo")"""

content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

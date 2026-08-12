import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = """        SettingsOptionRow(
            icon = Icons.Rounded.VideoSettings,
            title = "Qualidade de Streaming Padrão",
            value = streamQuality
        ) {
            val newQuality = if (streamQuality == "Automática") "Alta (1080p)" else "Automática"
            viewModel.setStreamQuality(newQuality)
        }"""

replace = """        if (accountExpiration.isNotEmpty()) {
            SettingsActionRow(
                icon = Icons.Rounded.DateRange,
                title = "Validade da Lista",
                subtitle = accountExpiration
            ) {
            }
        }"""

content = content.replace(target, replace)

# also remove streamQuality state to prevent unused variable warnings
content = content.replace('    val streamQuality by viewModel.streamQuality.collectAsState()\n', '')

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

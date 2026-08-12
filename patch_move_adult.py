import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = """        SettingsSwitchRow(
            icon = Icons.Rounded.Block,
            title = "Bloquear Conteúdo Adulto (+18)",
            subtitle = "Oculta categorias e canais que contenham conteúdo adulto.",
            checked = blockAdult
        ) { 
            viewModel.setBlockAdult(it)
        }"""
replace = """"""

content = content.replace(target, replace)
with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

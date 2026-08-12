import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Update phone nav bar
target_phone = """            Triple(Screen.FAVORITES, Icons.Rounded.Favorite, "Favoritos"),
            Triple(Screen.SETTINGS, Icons.Rounded.Settings, "Ajustes")"""
replace_phone = """            Triple(Screen.SETTINGS, Icons.Rounded.Settings, "Ajustes"),
            Triple(Screen.FAVORITES, Icons.Rounded.Favorite, "Favoritos")"""
content = content.replace(target_phone, replace_phone)

# Update TV sidebar
target_tv = """            TvSidebarItem(
                icon = Icons.Rounded.Favorite,
                label = "Favoritos",
                selected = currentScreen == Screen.FAVORITES
            ) { onNavigate(Screen.FAVORITES) }

            TvSidebarItem(
                icon = Icons.Rounded.FamilyRestroom,
                label = "Controle Parental",
                selected = currentScreen == Screen.PARENTAL_CONTROL
            ) { onNavigate(Screen.PARENTAL_CONTROL) }

            TvSidebarItem(
                icon = Icons.Rounded.Settings,
                label = "Configurações",
                selected = currentScreen == Screen.SETTINGS
            ) { onNavigate(Screen.SETTINGS) }"""
replace_tv = """            TvSidebarItem(
                icon = Icons.Rounded.FamilyRestroom,
                label = "Controle Parental",
                selected = currentScreen == Screen.PARENTAL_CONTROL
            ) { onNavigate(Screen.PARENTAL_CONTROL) }

            TvSidebarItem(
                icon = Icons.Rounded.Settings,
                label = "Configurações",
                selected = currentScreen == Screen.SETTINGS
            ) { onNavigate(Screen.SETTINGS) }

            TvSidebarItem(
                icon = Icons.Rounded.Favorite,
                label = "Favoritos",
                selected = currentScreen == Screen.FAVORITES
            ) { onNavigate(Screen.FAVORITES) }"""
content = content.replace(target_tv, replace_tv)

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

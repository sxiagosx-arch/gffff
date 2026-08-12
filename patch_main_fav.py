import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target = """                    // FULL SCREEN PLAYER OVERLAY
                    val watchHistory by viewModel.watchHistory.collectAsState(initial = emptyList())"""
replace = """                    // FULL SCREEN PLAYER OVERLAY
                    val watchHistory by viewModel.watchHistory.collectAsState(initial = emptyList())
                    val favorites by viewModel.favorites.collectAsState(initial = emptyList())"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target = """                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MatteBlack)
                ) {"""

replace = """                com.example.ui.components.PremiumBackground(
                    modifier = Modifier.fillMaxSize()
                ) {"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

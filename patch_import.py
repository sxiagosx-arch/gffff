import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.foundation.border" not in content:
    content = content.replace("import androidx.compose.foundation.background", "import androidx.compose.foundation.background\nimport androidx.compose.foundation.border")

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

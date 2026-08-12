with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix .androidx.compose.ui.focus.focusTarget() to .focusTarget()
content = content.replace(".androidx.compose.ui.focus.focusTarget()", ".focusTarget()")

# Add import if missing
if "import androidx.compose.ui.focus.focusTarget" not in content:
    content = content.replace("import androidx.compose.ui.focus.FocusRequester", "import androidx.compose.ui.focus.FocusRequester\nimport androidx.compose.ui.focus.focusTarget")

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

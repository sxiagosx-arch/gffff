import re
with open('app/src/main/java/com/example/ui/screens/SharedComponents.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.ui.graphics.Brush" not in content:
    content = content.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.graphics.Brush")
    
with open('app/src/main/java/com/example/ui/screens/SharedComponents.kt', 'w') as f:
    f.write(content)

import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                detectTapGestures(
                    onTap = {
                        if (!isLocked) {
                            showControls = !showControls
                        }
                        focusManager.clearFocus()
                    }
                )"""
replace = """                detectTapGestures(
                    onTap = {
                        showControls = !showControls
                        focusManager.clearFocus()
                    }
                )"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

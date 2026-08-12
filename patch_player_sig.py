import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """    inlineMode: Boolean = false,
    onChannelChange: (IPTVChannel) -> Unit,
    onFullscreen: () -> Unit = {}
)"""

replace = """    inlineMode: Boolean = false,
    isFav: Boolean = false,
    onToggleFav: () -> Unit = {},
    onChannelChange: (IPTVChannel) -> Unit,
    onFullscreen: () -> Unit = {}
)"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

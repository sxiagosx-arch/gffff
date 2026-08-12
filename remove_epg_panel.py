import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# Remove the whole block
epg_panel_pattern = r'        // EPG Panel on the Right[\s\S]*?        // Auto-Play Countdown Overlay'
content = re.sub(epg_panel_pattern, '        // Auto-Play Countdown Overlay', content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Right Top Actions
old_right_top = """                        // Right Top actions
                        Row {"""
new_right_top = """                        // Right Top actions
                        if (deviceLayoutMode != "TV") {
                        Row {"""
content = content.replace(old_right_top, new_right_top)

# Find the closing brace for Right Top actions
# Actually it's easier to just do it via regex or sed. But let's check what's after Right Top actions.

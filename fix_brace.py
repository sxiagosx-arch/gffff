with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re
# Find Slider closing parenthesis and the two `}` following it.
# We will just remove one `}`.
fixed = content.replace('modifier = Modifier.width(80.dp).height(24.dp).padding(end = 8.dp)\n                                )\n                                }', 'modifier = Modifier.width(80.dp).height(24.dp).padding(end = 8.dp)\n                                )')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(fixed)

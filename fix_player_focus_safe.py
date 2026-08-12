with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Remove the aggressive `if (deviceLayoutMode == "TV") true else false`
old_keys = """                    }
                }
                if (deviceLayoutMode == "TV") true else false
            }"""

new_keys = """                    }
                }
                false
            }"""

content = content.replace(old_keys, new_keys)

# Instead, add focusProperties to trap focus inside the player
content = content.replace('.then(if (deviceLayoutMode == "TV") Modifier.focusRequester(focusRequester).focusable() else Modifier)',
                          '.then(if (deviceLayoutMode == "TV") Modifier.focusRequester(focusRequester).androidx.compose.ui.focus.focusProperties { exit = { androidx.compose.ui.focus.FocusRequester.Cancel } }.focusable() else Modifier)')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

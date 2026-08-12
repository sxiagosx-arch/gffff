with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_player = """    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    DisposableEffect(Unit) {"""

new_player = """    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    
    LaunchedEffect(Unit) {
        if (deviceLayoutMode == "TV") {
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }

    DisposableEffect(Unit) {"""

content = content.replace(old_player, new_player)

# Also intercept all other keys if TV mode so it doesn't propagate to the UI below
old_keys = """                    }
                }
                false
            }"""

new_keys = """                    }
                }
                if (deviceLayoutMode == "TV") true else false
            }"""

content = content.replace(old_keys, new_keys)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

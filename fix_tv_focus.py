with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix 1: scaleMode default in TV mode to RESIZE_MODE_FILL
content = content.replace('var scaleMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }',
                          'var scaleMode by remember { mutableIntStateOf(if (deviceLayoutMode == "TV") androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL else androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT) }')
content = content.replace('AspectRatioFrameLayout.RESIZE_MODE_FIT', 'androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT')
content = content.replace('AspectRatioFrameLayout.RESIZE_MODE_FILL', 'androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL')
content = content.replace('AspectRatioFrameLayout.RESIZE_MODE_ZOOM', 'androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM')


# Fix 2: Key interception for TV mode
# When showChannelsList is true, we should NOT intercept DPAD_CENTER so the items can be clicked.
old_keys = """                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        showChannelsList = !showChannelsList
                        showControls = false
                        return@onKeyEvent true
                    }"""

new_keys = """                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (!showChannelsList && !showEPG) {
                            showChannelsList = true
                            showControls = false
                            return@onKeyEvent true
                        } else {
                            // Let the focused item handle the click
                        }
                    }"""
content = content.replace(old_keys, new_keys)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

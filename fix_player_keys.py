with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

old_keys = """            .onKeyEvent { event ->
                if (deviceLayoutMode == "TV" && event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    val keyCode = (event.nativeKeyEvent as android.view.KeyEvent).keyCode
                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        showChannelsList = !showChannelsList
                        showControls = false
                        return@onKeyEvent true
                    } else if (showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (channel.type == "LIVE") {
                            showEPG = true
                            showChannelsList = false
                        }
                        return@onKeyEvent true
                    } else if (keyCode == android.view.KeyEvent.KEYCODE_BACK || keyCode == android.view.KeyEvent.KEYCODE_ESCAPE) {
                        if (showChannelsList || showEPG) {
                            showChannelsList = false
                            showEPG = false
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }"""

new_keys = """            .onKeyEvent { event ->
                if (deviceLayoutMode == "TV" && event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    val keyCode = (event.nativeKeyEvent as android.view.KeyEvent).keyCode
                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        showChannelsList = !showChannelsList
                        showControls = false
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (channel.type == "LIVE") {
                            showEPG = true
                            showChannelsList = false
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP) {
                        val index = adjacentChannels.indexOfFirst { it.id == channel.id }
                        if (index > 0) onChannelChange(adjacentChannels[index - 1])
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN) {
                        val index = adjacentChannels.indexOfFirst { it.id == channel.id }
                        if (index != -1 && index < adjacentChannels.size - 1) onChannelChange(adjacentChannels[index + 1])
                        return@onKeyEvent true
                    } else if (keyCode == android.view.KeyEvent.KEYCODE_BACK || keyCode == android.view.KeyEvent.KEYCODE_ESCAPE) {
                        if (showChannelsList || showEPG || showControls) {
                            showChannelsList = false
                            showEPG = false
                            showControls = false
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }"""

content = content.replace(old_keys, new_keys)

# Hide lock, volume, fullscreen
# Volume slider is around line 645
import re
content = re.sub(r'(IconButton\(onClick = \{[^}]+\}\) \{\s*Icon\(\s*imageVector = if \(isMuted \|\| currentVolume == 0f\) Icons\.Rounded\.VolumeOff else if \(currentVolume < 0\.5f\) Icons\.Rounded\.VolumeDown else Icons\.Rounded\.VolumeUp,[^\)]+\)\s*\})', r'if (deviceLayoutMode != "TV") {\n                                    \1', content)
content = content.replace('Slider(\n                                    value = if (isMuted)', 'Slider(\n                                    value = if (isMuted)')
content = re.sub(r'(Slider\(\s*value = if \(isMuted\).*?modifier = Modifier\.width\(80\.dp\)\.height\(24\.dp\)\.padding\(end = 8\.dp\)\s*\))', r'\1\n                                }', content, flags=re.DOTALL)

# Hide Lock button
content = re.sub(r'(IconButton\(onClick = \{ isLocked = !isLocked \}\) \{\s*Icon\(\s*imageVector = if \(isLocked\) Icons\.Rounded\.Lock else Icons\.Rounded\.LockOpen,[^\)]+\)\s*\})', r'if (deviceLayoutMode != "TV") {\n                            \1\n                        }', content)

# Hide Fullscreen button
content = re.sub(r'(IconButton\(onClick = onFullscreen\) \{\s*Icon\(\s*imageVector = Icons\.Rounded\.Fullscreen,[^\)]+\)\s*\})', r'if (deviceLayoutMode != "TV") {\n                                    \1\n                                }', content)


with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_dpad = """                if (deviceLayoutMode == "TV" && event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    val keyCode = (event.nativeKeyEvent as android.view.KeyEvent).keyCode
                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (!showChannelsList && !showEPG) {
                            showChannelsList = true
                            showControls = false
                            return@onKeyEvent true
                        } else {
                            // Let the focused item handle the click
                        }
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (channel.type == "LIVE") {
                            showEPG = true
                            showChannelsList = false
                        } else {
                            exoPlayer?.let { player ->
                                val newPos = (player.currentPosition + 15000).coerceAtMost(player.duration)
                                player.seekTo(newPos)
                                showControls = true
                            }
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (channel.type != "LIVE") {
                            exoPlayer?.let { player ->
                                val newPos = (player.currentPosition - 15000).coerceAtLeast(0)
                                player.seekTo(newPos)
                                showControls = true
                            }
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
                }"""

new_dpad = """                if (deviceLayoutMode == "TV" && event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    val keyCode = (event.nativeKeyEvent as android.view.KeyEvent).keyCode
                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (showChannelsList) return@onKeyEvent false
                        
                        if (channel.type == "LIVE") {
                            showChannelsList = true
                            showControls = false
                        } else {
                            exoPlayer?.let { player ->
                                if (player.isPlaying) player.pause() else player.play()
                            }
                            showControls = true
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (channel.type != "LIVE") {
                            exoPlayer?.let { player ->
                                val newPos = (player.currentPosition + 15000).coerceAtMost(player.duration)
                                player.seekTo(newPos)
                                showControls = true
                            }
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (channel.type != "LIVE") {
                            exoPlayer?.let { player ->
                                val newPos = (player.currentPosition - 15000).coerceAtLeast(0)
                                player.seekTo(newPos)
                                showControls = true
                            }
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP) {
                        if (channel.type == "LIVE") {
                            val index = adjacentChannels.indexOfFirst { it.id == channel.id }
                            if (index > 0) {
                                onChannelChange(adjacentChannels[index - 1])
                                try { focusRequester.requestFocus() } catch (e: Exception) {}
                            }
                        } else {
                            showChannelsList = true
                            showControls = false
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (channel.type == "LIVE") {
                            val index = adjacentChannels.indexOfFirst { it.id == channel.id }
                            if (index != -1 && index < adjacentChannels.size - 1) {
                                onChannelChange(adjacentChannels[index + 1])
                                try { focusRequester.requestFocus() } catch (e: Exception) {}
                            }
                        } else {
                            showChannelsList = true
                            showControls = false
                        }
                        return@onKeyEvent true
                    } else if (keyCode == android.view.KeyEvent.KEYCODE_BACK || keyCode == android.view.KeyEvent.KEYCODE_ESCAPE) {
                        if (showChannelsList || showControls) {
                            showChannelsList = false
                            showControls = false
                            try { focusRequester.requestFocus() } catch (e: Exception) {}
                            return@onKeyEvent true
                        }
                    }
                }"""

content = content.replace(old_dpad, new_dpad)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

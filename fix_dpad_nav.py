with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

old_right = """                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (channel.type == "LIVE") {
                            showEPG = true
                            showChannelsList = false
                        }
                        return@onKeyEvent true"""

new_right = """                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
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
                        return@onKeyEvent true"""

content = content.replace(old_right, new_right)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

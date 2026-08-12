with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix Live Channels Row
old_live = """                                    .border(if (isFocused) 3.dp else 1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                    .clickable { onChannelChange(ch) }
                                    .padding(16.dp),"""
new_live = """                                    .border(if (isFocused) 3.dp else 1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .onKeyEvent { event ->
                                        if (event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp && 
                                            (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                                             event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                                            onChannelChange(ch)
                                            true
                                        } else false
                                    }
                                    .clickable { onChannelChange(ch) }
                                    .padding(16.dp),"""
content = content.replace(old_live, new_live)

# Fix Series Season Episodes
old_episodes = """                                            .border(if (isFocused) 2.dp else 1.dp, if (isFocused) Color.White else NeonGreen, RoundedCornerShape(8.dp))
                                            .onFocusChanged { isFocused = it.isFocused }
                                            .focusable()
                                            .clickable { onChannelChange(ch) },"""
new_episodes = """                                            .border(if (isFocused) 2.dp else 1.dp, if (isFocused) Color.White else NeonGreen, RoundedCornerShape(8.dp))
                                            .onFocusChanged { isFocused = it.isFocused }
                                            .onKeyEvent { event ->
                                                if (event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp && 
                                                    (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                                                     event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                                                    onChannelChange(ch)
                                                    true
                                                } else false
                                            }
                                            .clickable { onChannelChange(ch) },"""
content = content.replace(old_episodes, new_episodes)

# Fix EPG List
old_epg = """                                    .border(if (isFocused) 2.dp else 0.dp, NeonGreen, RoundedCornerShape(8.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                    .padding(12.dp)"""
new_epg = """                                    .border(if (isFocused) 2.dp else 0.dp, NeonGreen, RoundedCornerShape(8.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .focusable() // EPG is just focusable, no click action required currently? Wait, EPG is not clickable right now.
                                    .padding(12.dp)"""
content = content.replace(old_epg, new_epg)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

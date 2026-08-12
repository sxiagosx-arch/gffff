with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix Live Channels list click logic
old_live = """                                            onChannelChange(ch)
                                            true
                                        } else false
                                    }
                                    .clickable { onChannelChange(ch) }
                                    .padding(16.dp),"""
new_live = """                                            onChannelChange(ch)
                                            showChannelsList = false
                                            if (deviceLayoutMode == "TV") {
                                                try { focusRequester.requestFocus() } catch (e: Exception) {}
                                            }
                                            true
                                        } else false
                                    }
                                    .clickable { 
                                        onChannelChange(ch)
                                        showChannelsList = false
                                    }
                                    .padding(16.dp),"""
content = content.replace(old_live, new_live)

# Fix Episode list with seasons
old_ep_season = """                                                    onChannelChange(ch)
                                                    true
                                                } else false
                                            }
                                            .clickable { onChannelChange(ch) },"""
new_ep_season = """                                                    onChannelChange(ch)
                                                    showChannelsList = false
                                                    if (deviceLayoutMode == "TV") {
                                                        try { focusRequester.requestFocus() } catch (e: Exception) {}
                                                    }
                                                    true
                                                } else false
                                            }
                                            .focusable()
                                            .clickable { 
                                                onChannelChange(ch)
                                                showChannelsList = false
                                            },"""
content = content.replace(old_ep_season, new_ep_season)

# Fix Episode list without seasons (fallback)
old_ep_no_season = """                                Box(
                                    modifier = Modifier
                                        .width(64.dp)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonGreen else Color.Black.copy(alpha = 0.65f))
                                        .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                        .clickable { onChannelChange(ch) },"""
new_ep_no_season = """                                var isFocused by remember { mutableStateOf(false) }
                                Box(
                                    modifier = Modifier
                                        .then(if (isSelected) Modifier.focusRequester(episodeListFocusRequester) else Modifier)
                                        .width(64.dp)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonGreen else if (isFocused) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.65f))
                                        .border(if (isFocused) 2.dp else 1.dp, if (isFocused) Color.White else NeonGreen, RoundedCornerShape(8.dp))
                                        .onFocusChanged { isFocused = it.isFocused }
                                        .onKeyEvent { event ->
                                            if (event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp && 
                                                (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                                                 event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                                                onChannelChange(ch)
                                                showChannelsList = false
                                                if (deviceLayoutMode == "TV") {
                                                    try { focusRequester.requestFocus() } catch (e: Exception) {}
                                                }
                                                true
                                            } else false
                                        }
                                        .focusable()
                                        .clickable { 
                                            onChannelChange(ch)
                                            showChannelsList = false
                                        },"""
content = content.replace(old_ep_no_season, new_ep_no_season)

# Add focusable back to Live Channels if missing
content = content.replace(".onFocusChanged { isFocused = it.isFocused }", ".onFocusChanged { isFocused = it.isFocused }\n                                    .focusable()")

# Fix the duplicate focusable that might happen
content = content.replace(".focusable()\n                                    .focusable()", ".focusable()")

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

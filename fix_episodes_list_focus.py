with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Add a FocusRequester for the current selected episode
old_lazyrow = """                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),"""

new_lazyrow = """                    val episodeListFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
                    LaunchedEffect(showChannelsList) {
                        if (showChannelsList && deviceLayoutMode == "TV") {
                            try { episodeListFocusRequester.requestFocus() } catch (e: Exception) {}
                        }
                    }
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),"""

content = content.replace(old_lazyrow, new_lazyrow)

# And attach it to the current selected episode
old_ep_box = """                                    val epNumMatch = Regex("(?i)(?:E|EP|Episódio|Episode)\\\\s*(\\\\d+)").find(ch.name)
                                    val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                                    var isFocused by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)"""

new_ep_box = """                                    val epNumMatch = Regex("(?i)(?:E|EP|Episódio|Episode)\\\\s*(\\\\d+)").find(ch.name)
                                    val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                                    var isFocused by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .then(if (isSelected) Modifier.focusRequester(episodeListFocusRequester) else Modifier)
                                            .width(64.dp)"""

content = content.replace(old_ep_box, new_ep_box)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

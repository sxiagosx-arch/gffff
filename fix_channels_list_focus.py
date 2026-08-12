with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_lazycol = """                    LazyColumn(
                        modifier = Modifier.weight(1f),"""

new_lazycol = """                    val channelListFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
                    LaunchedEffect(showChannelsList) {
                        if (showChannelsList && deviceLayoutMode == "TV") {
                            try { channelListFocusRequester.requestFocus() } catch (e: Exception) {}
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),"""

content = content.replace(old_lazycol, new_lazycol)

old_row = """                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()"""

new_row = """                            Row(
                                modifier = Modifier
                                    .then(if (isSelected) Modifier.focusRequester(channelListFocusRequester) else Modifier)
                                    .fillMaxWidth()"""

content = content.replace(old_row, new_row)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

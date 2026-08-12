with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix channel list row
old_ch_row = """                        items(adjacentChannels) { ch ->
                            val isSelected = ch.id == channel.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonGreen else Color.Black)
                                    .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .clickable { onChannelChange(ch) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ch.name,
                                    color = if (isSelected) Color.Black else NeonGreen,"""

new_ch_row = """                        items(adjacentChannels) { ch ->
                            val isSelected = ch.id == channel.id
                            var isFocused by remember { mutableStateOf(false) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isFocused) NeonGreen.copy(alpha=0.5f) else if (isSelected) NeonGreen else Color.Black)
                                    .border(if (isFocused) 3.dp else 1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                    .clickable { onChannelChange(ch) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ch.name,
                                    color = if (isSelected || isFocused) Color.Black else NeonGreen,"""

content = content.replace(old_ch_row, new_ch_row)

# Fix EPG list column
old_epg = """                        items(epgList) { prog ->
                            val isCurrent = currentTime in prog.startTimestamp..prog.stopTimestamp
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) NeonGreenDim else Color.White.copy(alpha = 0.05f))
                                    .padding(12.dp)
                            ) {"""

new_epg = """                        items(epgList) { prog ->
                            val isCurrent = currentTime in prog.startTimestamp..prog.stopTimestamp
                            var isFocused by remember { mutableStateOf(false) }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isFocused) NeonGreen.copy(alpha=0.3f) else if (isCurrent) NeonGreenDim else Color.White.copy(alpha = 0.05f))
                                    .border(if (isFocused) 2.dp else 0.dp, NeonGreen, RoundedCornerShape(8.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                    .padding(12.dp)
                            ) {"""

content = content.replace(old_epg, new_epg)
content = content.replace('import androidx.compose.ui.focus.focusRequester', 'import androidx.compose.ui.focus.focusRequester\nimport androidx.compose.ui.focus.onFocusChanged')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Add focus state to episodes
old_ep_box = """                                    val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                                    
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) NeonGreen else Color.Black.copy(alpha = 0.65f))
                                            .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                            .clickable { onChannelChange(ch) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayNum,
                                            color = if (isSelected) Color.Black else NeonGreen,"""

new_ep_box = """                                    val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                                    var isFocused by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) NeonGreen else if (isFocused) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.65f))
                                            .border(if (isFocused) 2.dp else 1.dp, if (isFocused) Color.White else NeonGreen, RoundedCornerShape(8.dp))
                                            .onFocusChanged { isFocused = it.isFocused }
                                            .clickable { onChannelChange(ch) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayNum,
                                            color = if (isSelected) Color.Black else if (isFocused) Color.White else NeonGreen,"""

if old_ep_box in content:
    content = content.replace(old_ep_box, new_ep_box)
else:
    print("Could not find old_ep_box")

# Also do it for the Channel List (LIVE TV)
old_ch_box = """                            val isSelected = ch.id == channel.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { onChannelChange(ch) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ch.logo,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ch.name,
                                    color = if (isSelected) NeonGreen else Color.White,"""

new_ch_box = """                            val isSelected = ch.id == channel.id
                            var isFocused by remember { mutableStateOf(false) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else if (isFocused) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(if (isFocused) 1.dp else 0.dp, if (isFocused) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .clickable { onChannelChange(ch) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ch.logo,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ch.name,
                                    color = if (isSelected) NeonGreen else if (isFocused) Color.White else Color.White,"""
if old_ch_box in content:
    content = content.replace(old_ch_box, new_ch_box)
else:
    print("Could not find old_ch_box")

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonGreenDim else Color.Black.copy(alpha = 0.8f))
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonGreen else Color.Gray.copy(alpha=0.5f), RoundedCornerShape(8.dp))
                                .clickable { onChannelChange(ch) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayNum,
                                color = if (isSelected) NeonGreen else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }"""
replace = """                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonGreen else Color.Black.copy(alpha = 0.8f))
                                .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                .clickable { onChannelChange(ch) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayNum,
                                color = if (isSelected) Color.Black else NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

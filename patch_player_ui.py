import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# 1. Back button
target_back = """                            IconButton(onClick = onClose) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))"""
replace_back = """                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))"""
content = content.replace(target_back, replace_back)

# 2. Next Episode Popup at the end of the main Box
target_end = """        }

        // Tech Info"""
replace_end = """        }

        // Next Episode Popup
        val timeLeftMs = totalDuration - currentPosition
        if (channel.type != "LIVE" && totalDuration > 0 && timeLeftMs in 1L..240000L) {
            val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
            val nextChannel = if (currIndex != -1 && currIndex < adjacentChannels.size - 1) adjacentChannels[currIndex + 1] else null
            if (nextChannel != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showControls) 120.dp else 32.dp, end = 32.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                            .clickable { onChannelChange(nextChannel) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(if (channel.type == "SERIES") "Próximo Episódio" else "Próximo Filme", color = Color.LightGray, fontSize = 12.sp)
                            Text(nextChannel.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Pular agora (${(timeLeftMs / 1000).toInt()}s)", color = NeonGreen, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = "Próximo", tint = NeonGreen, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }

        // Tech Info"""
content = content.replace(target_end, replace_end)

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                                IconButton(onClick = { 
                                    if (currentVolume == 0f) {
                                        currentVolume = 1f
                                        isMuted = false
                                    } else {
                                        isMuted = !isMuted
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isMuted || currentVolume == 0f) Icons.Rounded.VolumeOff else if (currentVolume < 0.5f) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp,
                                        contentDescription = "Mudo",
                                        tint = if (isMuted || currentVolume == 0f) Color.White else NeonGreen
                                    )
                                }

                            }"""

replace = """                                IconButton(onClick = { 
                                    if (currentVolume == 0f) {
                                        currentVolume = 1f
                                        isMuted = false
                                    } else {
                                        isMuted = !isMuted
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isMuted || currentVolume == 0f) Icons.Rounded.VolumeOff else if (currentVolume < 0.5f) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp,
                                        contentDescription = "Mudo",
                                        tint = if (isMuted || currentVolume == 0f) Color.White else NeonGreen
                                    )
                                }
                                
                                Slider(
                                    value = if (isMuted) 0f else currentVolume,
                                    onValueChange = { vol ->
                                        currentVolume = vol
                                        isMuted = vol == 0f
                                    },
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonGreen,
                                        activeTrackColor = NeonGreen,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                                        activeTickColor = Color.Transparent,
                                        inactiveTickColor = Color.Transparent
                                    ),
                                    modifier = Modifier.width(80.dp).height(24.dp).padding(end = 8.dp)
                                )
                            }"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

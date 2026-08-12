import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# Remove the FLAG_SHOW_UI
content = content.replace("AudioManager.FLAG_SHOW_UI", "0")

# The user might be referring to the horizontal volume slider in the bottom controls, let's remove it and only keep the mute button.
target_slider = """                                Slider(
                                    value = if (isMuted) 0f else currentVolume,
                                    onValueChange = { vol ->
                                        currentVolume = vol
                                        isMuted = vol == 0f
                                    },
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonGreen,
                                        activeTrackColor = NeonGreen,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.width(100.dp).padding(end = 8.dp)
                                )"""
replace_slider = """"""
content = content.replace(target_slider, replace_slider)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

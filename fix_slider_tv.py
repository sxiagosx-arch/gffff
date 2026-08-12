with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_slider = """                            Slider(
                                value = currentPosition.toFloat(),
                                onValueChange = { pos ->
                                    currentPosition = pos.toLong()
                                    exoPlayer?.seekTo(currentPosition)
                                },
                                valueRange = 0f..totalDuration.toFloat(),
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonGreen,
                                    activeTrackColor = NeonGreen,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )"""

new_slider = """                            if (deviceLayoutMode == "TV") {
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f,
                                    color = NeonGreen,
                                    trackColor = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.fillMaxWidth().height(4.dp)
                                )
                            } else {
                                Slider(
                                    value = currentPosition.toFloat(),
                                    onValueChange = { pos ->
                                        currentPosition = pos.toLong()
                                        exoPlayer?.seekTo(currentPosition)
                                    },
                                    valueRange = 0f..totalDuration.toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonGreen,
                                        activeTrackColor = NeonGreen,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }"""

content = content.replace(old_slider, new_slider)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_inline = """                        if (inlineMode) {
                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Fullscreen,
                                    contentDescription = "Tela Cheia",
                                    tint = Color.White
                                )
                            }
                        }"""

new_inline = """                        if (inlineMode) {
                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Fullscreen,
                                    contentDescription = "Tela Cheia",
                                    tint = Color.White
                                )
                            }
                        } else if (deviceLayoutMode == "TV") {
                            IconButton(
                                onClick = {
                                    scaleMode = when (scaleMode) {
                                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                        else -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = when (scaleMode) {
                                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT -> Icons.Rounded.Fullscreen
                                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL -> Icons.Rounded.OpenInFull
                                        else -> Icons.Rounded.ZoomIn
                                    },
                                    contentDescription = "Perspectiva / Tela Cheia",
                                    tint = NeonGreen
                                )
                            }
                        }"""
content = content.replace(old_inline, new_inline)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

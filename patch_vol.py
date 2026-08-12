import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target1 = """    var isLocked by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var scaleMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }"""
replace1 = """    var isLocked by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var currentVolume by remember { mutableFloatStateOf(1f) }
    var scaleMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }"""
content = content.replace(target1, replace1)

target2 = """    // Initialize Player
    LaunchedEffect(isMuted) {
        exoPlayer?.volume = if (isMuted) 0f else 1f
    }"""
replace2 = """    // Initialize Player
    LaunchedEffect(isMuted, currentVolume) {
        exoPlayer?.volume = if (isMuted) 0f else currentVolume
    }"""
content = content.replace(target2, replace2)

target3 = """                            IconButton(onClick = { isMuted = !isMuted }) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                                    contentDescription = "Mudo",
                                    tint = if (isMuted) NeonGreen else Color.White
                                )
                            }"""
replace3 = """                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { 
                                    if (currentVolume == 0f) {
                                        currentVolume = 1f
                                        isMuted = false
                                    } else {
                                        isMuted = !isMuted
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isMuted || currentVolume == 0f) Icons.AutoMirrored.Rounded.VolumeOff else if (currentVolume < 0.5f) Icons.Rounded.VolumeDown else Icons.AutoMirrored.Rounded.VolumeUp,
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
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.width(100.dp).padding(end = 8.dp)
                                )
                            }"""
content = content.replace(target3, replace3)

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

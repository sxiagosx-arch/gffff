with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# Instead of regex, I'll use simple replace to be safe.
lock_old = """IconButton(onClick = { isLocked = !isLocked }) {
                            Icon(
                                imageVector = if (isLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                                contentDescription = "Lock",
                                tint = Color.White
                            )
                        }"""
lock_new = """if (deviceLayoutMode != "TV") {
                            IconButton(onClick = { isLocked = !isLocked }) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                                    contentDescription = "Lock",
                                    tint = Color.White
                                )
                            }
                        }"""
content = content.replace(lock_old, lock_new)

vol_btn_old = """IconButton(onClick = {
                                    if (currentVolume == 0f) {
                                        currentVolume = 1f
                                        isMuted = false
                                    } else {
                                        isMuted = !isMuted
                                    }
                                    val targetVol = if (isMuted) 0 else (currentVolume * maxVolume).toInt()
                                    audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVol, 0)
                                }) {
                                    Icon(
                                        imageVector = if (isMuted || currentVolume == 0f) Icons.AutoMirrored.Rounded.VolumeOff else if (currentVolume < 0.5f) Icons.AutoMirrored.Rounded.VolumeDown else Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = "Mudo",
                                        tint = if (isMuted || currentVolume == 0f) Color.White else NeonGreen
                                    )
                                }
                                
                                Slider(
                                    value = if (isMuted) 0f else currentVolume,
                                    onValueChange = { vol ->
                                        currentVolume = vol
                                        isMuted = vol == 0f
                                        val targetVol = if (isMuted) 0 else (currentVolume * maxVolume).toInt()
                                        audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVol, 0)
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
                                )"""
vol_btn_new = """if (deviceLayoutMode != "TV") {
                                IconButton(onClick = {
                                    if (currentVolume == 0f) {
                                        currentVolume = 1f
                                        isMuted = false
                                    } else {
                                        isMuted = !isMuted
                                    }
                                    val targetVol = if (isMuted) 0 else (currentVolume * maxVolume).toInt()
                                    audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVol, 0)
                                }) {
                                    Icon(
                                        imageVector = if (isMuted || currentVolume == 0f) Icons.AutoMirrored.Rounded.VolumeOff else if (currentVolume < 0.5f) Icons.AutoMirrored.Rounded.VolumeDown else Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = "Mudo",
                                        tint = if (isMuted || currentVolume == 0f) Color.White else NeonGreen
                                    )
                                }
                                
                                Slider(
                                    value = if (isMuted) 0f else currentVolume,
                                    onValueChange = { vol ->
                                        currentVolume = vol
                                        isMuted = vol == 0f
                                        val targetVol = if (isMuted) 0 else (currentVolume * maxVolume).toInt()
                                        audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVol, 0)
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
content = content.replace(vol_btn_old, vol_btn_new)

fs_old = """IconButton(onClick = onFullscreen) {
                                    Icon(
                                        imageVector = Icons.Rounded.Fullscreen,
                                        contentDescription = "Tela Cheia",
                                        tint = Color.White
                                    )
                                }"""
fs_new = """if (deviceLayoutMode != "TV") {
                                    IconButton(onClick = onFullscreen) {
                                        Icon(
                                            imageVector = Icons.Rounded.Fullscreen,
                                            contentDescription = "Tela Cheia",
                                            tint = Color.White
                                        )
                                    }
                                }"""
content = content.replace(fs_old, fs_new)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

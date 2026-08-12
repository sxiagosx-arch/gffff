with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                            IconButton(onClick = { isLocked = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,"""
replace = """                            var expandedQuality by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expandedQuality = true }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Qualidade",
                                        tint = Color.White
                                    )
                                }
                                androidx.compose.material3.DropdownMenu(
                                    expanded = expandedQuality,
                                    onDismissRequest = { expandedQuality = false },
                                    modifier = Modifier.background(com.example.ui.theme.Charcoal)
                                ) {
                                    val qualities = listOf(
                                        "Automática" to Pair(Int.MAX_VALUE, Int.MAX_VALUE),
                                        "1080p" to Pair(1920, 1080),
                                        "720p" to Pair(1280, 720),
                                        "480p" to Pair(854, 480)
                                    )
                                    qualities.forEach { (label, res) ->
                                        androidx.compose.material3.DropdownMenuItem(
                                            text = { androidx.compose.material3.Text(label, color = Color.White) },
                                            onClick = {
                                                exoPlayer?.let { player ->
                                                    val params = player.trackSelectionParameters.buildUpon()
                                                    if (label == "Automática") {
                                                        params.clearVideoSizeConstraints()
                                                    } else {
                                                        params.setMaxVideoSize(res.first, res.second)
                                                    }
                                                    player.trackSelectionParameters = params.build()
                                                }
                                                expandedQuality = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            IconButton(onClick = { isLocked = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,"""

content = content.replace(target, replace)

if "import androidx.compose.material.icons.rounded.Settings" not in content:
    content = content.replace("import androidx.compose.material.icons.rounded.*", "import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.rounded.Settings")

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

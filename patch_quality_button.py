import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                            var expandedQuality by remember { mutableStateOf(false) }
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
                            }"""

replace = """                            var expandedQuality by remember { mutableStateOf(false) }
                            var currentQualityLabel by remember { mutableStateOf("Automática") }
                            Box {
                                androidx.compose.material3.TextButton(
                                    onClick = { expandedQuality = true },
                                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(imageVector = Icons.Rounded.HighQuality, contentDescription = "Qualidade")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = currentQualityLabel, fontWeight = FontWeight.Bold)
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
                                            text = { 
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    if (currentQualityLabel == label) {
                                                        Icon(Icons.Rounded.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                    }
                                                    androidx.compose.material3.Text(label, color = if (currentQualityLabel == label) NeonGreen else Color.White)
                                                }
                                            },
                                            onClick = {
                                                currentQualityLabel = label
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
                            }"""

content = content.replace(target, replace)
if "import androidx.compose.material.icons.rounded.HighQuality" not in content:
    content = content.replace("import androidx.compose.material.icons.rounded.*", "import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.rounded.HighQuality\nimport androidx.compose.material.icons.rounded.Check")

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

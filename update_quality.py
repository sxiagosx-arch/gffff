import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                                            onClick = {
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
                                            }"""

replace = """                                            onClick = {
                                                exoPlayer?.let { player ->
                                                    var hasMultipleTracks = false
                                                    for (group in player.currentTracks.groups) {
                                                        if (group.type == androidx.media3.common.C.TRACK_TYPE_VIDEO && group.length > 1) {
                                                            hasMultipleTracks = true
                                                            break
                                                        }
                                                    }
                                                    
                                                    if (!hasMultipleTracks && label != "Automática") {
                                                        android.widget.Toast.makeText(context, "Qualidade fixa pelo servidor, não é possível alterar.", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                    
                                                    currentQualityLabel = label
                                                    val params = player.trackSelectionParameters.buildUpon()
                                                    if (label == "Automática") {
                                                        params.clearVideoSizeConstraints()
                                                        params.setForceHighestSupportedBitrate(false)
                                                    } else {
                                                        params.setMaxVideoSize(res.first, res.second)
                                                    }
                                                    player.trackSelectionParameters = params.build()
                                                }
                                                expandedQuality = false
                                            }"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                    // CENTER PLAYBACK / CHANNELS SWITCHERS
                    Row("""

replace = """                    // CENTER PLAYBACK / CHANNELS SWITCHERS
                    if (deviceLayoutMode != "TV") {
                    Row("""

target_end = """                        }
                    }

                    // Bottom Seekbar (For VOD/Series)"""

replace_end = """                        }
                    }
                    }

                    // Bottom Seekbar (For VOD/Series)"""

content = content.replace(target, replace)
content = content.replace(target_end, replace_end)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

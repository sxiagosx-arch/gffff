import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """                        }
                    }
                    // BOTTOM SEEKBAR / TIME PROGRESS"""

replace = """                        }
                    }
                    }
                    // BOTTOM SEEKBAR / TIME PROGRESS"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

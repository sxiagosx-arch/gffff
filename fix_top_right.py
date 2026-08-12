with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_right_top = """                        // Right Top actions
                        Row {"""
new_right_top = """                        // Right Top actions
                        if (deviceLayoutMode != "TV") {
                        Row {"""

old_end_right_top = """                            IconButton(onClick = { isLocked = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Bloquear Tela",
                                    tint = Color.White
                                )
                            }
                        }
                    }"""

new_end_right_top = """                            IconButton(onClick = { isLocked = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Bloquear Tela",
                                    tint = Color.White
                                )
                            }
                        }
                        }
                    }"""

if old_right_top in content and old_end_right_top in content:
    content = content.replace(old_right_top, new_right_top)
    content = content.replace(old_end_right_top, new_end_right_top)
    with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
        f.write(content)
else:
    print("Could not find right top to replace")

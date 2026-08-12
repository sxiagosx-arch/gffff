with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# Make showControls false by default
content = content.replace("var showControls by remember { mutableStateOf(true) }", "var showControls by remember { mutableStateOf(false) }")

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

content = content.replace('    LaunchedEffect(channel.id) {\n        dismissNextPopup = false\n    }', 
                          '    LaunchedEffect(channel.id) {\n        dismissNextPopup = false\n        showChannelsList = false\n    }')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

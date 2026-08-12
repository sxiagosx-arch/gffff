with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

content = content.replace('androidx.media3.ui.androidx.media3.ui.AspectRatioFrameLayout', 'androidx.media3.ui.AspectRatioFrameLayout')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

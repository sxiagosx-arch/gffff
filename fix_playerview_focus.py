with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

content = content.replace('androidx.media3.ui.PlayerView(ctx).apply {\n                    useController = false',
                          'androidx.media3.ui.PlayerView(ctx).apply {\n                    useController = false\n                    isFocusable = false\n                    isFocusableInTouchMode = false\n                    descendantFocusability = android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

content = content.replace('.onFocusChanged { isFocused = it.isFocused }\n                                            .clickable { onChannelChange(ch) },', 
                          '.onFocusChanged { isFocused = it.isFocused }\n                                            .focusable()\n                                            .clickable { onChannelChange(ch) },')

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

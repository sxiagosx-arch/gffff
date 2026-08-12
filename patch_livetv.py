import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt', 'r') as f:
    content = f.read()

# Remove previewChannel state and LaunchedEffect
target_preview_state = """    var previewChannel by remember { mutableStateOf<IPTVChannel?>(null) }
    
    // Auto-select first channel for preview
    LaunchedEffect(filteredChannels) {
        if (previewChannel == null && filteredChannels.isNotEmpty()) {
            val first = filteredChannels.first()
            previewChannel = first
            // viewModel.selectChannel(first) removed to prevent auto-fullscreen
        }
    }"""
content = content.replace(target_preview_state, "")

# Remove the INLINE PLAYER AT THE TOP
target_inline_player = """            // INLINE PLAYER AT THE TOP
            if (previewChannel != null) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.Black)) {
                    com.example.ui.player.CustomIPTVPlayer(
                        channel = previewChannel!!,
                        adjacentChannels = filteredChannels,
                        epgList = emptyList(), // EPG handled externally now
                        bufferSize = viewModel.bufferSize.collectAsState().value,
                        inlineMode = true,
                        onClose = { previewChannel = null },
                        onSaveProgress = { _, _ -> },
                        onChannelChange = { newCh -> 
                            previewChannel = newCh 
                            // viewModel.selectChannel(newCh) removed to keep inline
                        },
                        onFullscreen = { 
                            val ch = previewChannel
                            previewChannel = null
                            viewModel.selectChannel(ch) 
                        }
                    )
                }
            }"""
content = content.replace(target_inline_player, "")

# Change the onClick behavior
target_click = """                                    onClick = { 
                                        previewChannel = ch
                                        // viewModel.selectChannel(ch) removed
                                    }"""
replace_click = """                                    onClick = { 
                                        viewModel.selectChannel(ch) 
                                    }"""
content = content.replace(target_click, replace_click)

# Fix isSelected in LiveChannelListItem
target_selected = """                                val isSelected = previewChannel?.id == ch.id"""
replace_selected = """                                val isSelected = false"""
content = content.replace(target_selected, replace_selected)

with open('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# 1. Update onTap
target_on_tap = """                    onTap = {
                        showControls = !showControls
                        focusManager.clearFocus()
                    }"""
replace_on_tap = """                    onTap = {
                        if (showChannelsList || showEPG) {
                            showChannelsList = false
                            showEPG = false
                        } else {
                            showControls = !showControls
                        }
                        focusManager.clearFocus()
                    }"""
content = content.replace(target_on_tap, replace_on_tap)

# 2. Update Player Controls visibility
target_controls_vis = """        // Player Controls (Neon Matte Overlay)
        AnimatedVisibility(
            visible = showControls,"""
replace_controls_vis = """        // Player Controls (Neon Matte Overlay)
        AnimatedVisibility(
            visible = showControls && !(showChannelsList && channel.type == "SERIES"),"""
content = content.replace(target_controls_vis, replace_controls_vis)

# 3. Update Episodes List Panel
target_episodes_list = """        // Episodes List Panel on the Bottom (SERIES)
        AnimatedVisibility(
            visible = !isLocked && !inlineMode && showChannelsList && adjacentChannels.isNotEmpty() && channel.type == "SERIES",
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 140.dp) // above the bottom controls
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {"""
replace_episodes_list = """        // Episodes List Panel on the Bottom (SERIES)
        AnimatedVisibility(
            visible = !isLocked && !inlineMode && showChannelsList && adjacentChannels.isNotEmpty() && channel.type == "SERIES",
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(
                            onClick = { 
                                showChannelsList = false 
                                focusManager.clearFocus()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Fechar Episódios", tint = NeonGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {"""
content = content.replace(target_episodes_list, replace_episodes_list)

# 4. Close the Column
target_lazy_row_end = """                            )
                        }
                    }
                }
            }
        }

        // Up Next Toast Overlay"""
replace_lazy_row_end = """                            )
                        }
                    }
                }
                }
            }
        }

        // Up Next Toast Overlay"""
content = content.replace(target_lazy_row_end, replace_lazy_row_end)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

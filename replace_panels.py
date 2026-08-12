import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = r"        // Channels List Panel on the Left.*?        // Next Episode Popup"

replace = """        // Channels List Panel on the Left (LIVE TV)
        AnimatedVisibility(
            visible = showControls && !isLocked && !inlineMode && showChannelsList && adjacentChannels.isNotEmpty() && channel.type != "SERIES",
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .border(2.dp, NeonGreen, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Canais",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        androidx.compose.material3.Button(
                            onClick = { 
                                showChannelsList = false 
                                focusManager.clearFocus()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .testTag("close_episodes_list")
                                .focusable(true)
                        ) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Fechar", tint = Color.White)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(adjacentChannels) { ch ->
                            val isSelected = ch.id == channel.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonGreenDim else Color.White.copy(alpha = 0.05f))
                                    .border(if (isSelected) 1.dp else 0.dp, if (isSelected) NeonGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { onChannelChange(ch) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ch.name,
                                    color = if (isSelected) NeonGreen else Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Episodes List Panel on the Bottom (SERIES)
        AnimatedVisibility(
            visible = showControls && !isLocked && !inlineMode && showChannelsList && adjacentChannels.isNotEmpty() && channel.type == "SERIES",
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
                ) {
                    items(adjacentChannels) { ch ->
                        val isSelected = ch.id == channel.id
                        // Try to extract episode number, else fallback to name
                        val epNumMatch = Regex("(?i)(?:E|EP|Episódio|Episode)\\\\s*(\\\\d+)").find(ch.name)
                        val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                        
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonGreenDim else Color.Black.copy(alpha = 0.8f))
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonGreen else Color.Gray.copy(alpha=0.5f), RoundedCornerShape(8.dp))
                                .clickable { onChannelChange(ch) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayNum,
                                color = if (isSelected) NeonGreen else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Next Episode Popup"""

content = re.sub(target, replace, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

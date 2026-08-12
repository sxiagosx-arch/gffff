import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# 1. Channels List Panel on the Left (for Live TV)
target_channels_panel = """        AnimatedVisibility(
            visible = showControls && !isLocked && !inlineMode && showChannelsList && adjacentChannels.isNotEmpty(),
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
                    .background(Color.Black)
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
                            text = if (channel.type == "SERIES") "Episódios" else "Canais",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
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
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fechar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                    items(adjacentChannels) { ch ->
                        val isSelected = ch.id == channel.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonGreenDim else Color.Transparent)
                                .clickable { onChannelChange(ch) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ch.name,
                                color = if (isSelected) NeonGreen else Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                }
            }
        }"""

replace_channels_panel = """        // Channels List Panel on the Left (LIVE TV)
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
                    androidx.compose.foundation.lazy.LazyColumn(
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
                .height(140.dp)
                .padding(bottom = 60.dp) // above the bottom controls
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .border(1.dp, NeonGreen, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Episódios", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { showChannelsList = false }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Fechar", tint = Color.White)
                        }
                    }
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(adjacentChannels) { ch ->
                            val isSelected = ch.id == channel.id
                            // Try to extract episode number, else fallback to name
                            val epNumMatch = Regex("(?i)(?:E|EP|Episódio|Episode)\\s*(\\d+)").find(ch.name)
                            val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                            
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonGreenDim else Color.White.copy(alpha = 0.1f))
                                    .border(if (isSelected) 2.dp else 0.dp, if (isSelected) NeonGreen else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { onChannelChange(ch) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (epNumMatch != null) "EP $displayNum" else displayNum,
                                    color = if (isSelected) NeonGreen else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }"""

content = content.replace(target_channels_panel, replace_channels_panel)

target_next_popup = """        // Next Episode Popup
        val timeLeftMs = totalDuration - currentPosition
        if (channel.type != "LIVE" && totalDuration > 0 && timeLeftMs in 1L..240000L) {
            val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
            val nextChannel = if (currIndex != -1 && currIndex < adjacentChannels.size - 1) adjacentChannels[currIndex + 1] else null
            if (nextChannel != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showControls) 120.dp else 32.dp, end = 32.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                            .clickable { onChannelChange(nextChannel) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(if (channel.type == "SERIES") "Próximo Episódio" else "Próximo Filme", color = Color.LightGray, fontSize = 12.sp)
                            Text(nextChannel.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Pular agora (${(timeLeftMs / 1000).toInt()}s)", color = NeonGreen, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = "Próximo", tint = NeonGreen)
                    }
                }
            }
        }"""

replace_next_popup = """        // Next Episode / Back to Movie Menu Popup
        val timeLeftMs = totalDuration - currentPosition
        if (channel.type != "LIVE" && totalDuration > 0 && timeLeftMs in 1L..240000L) {
            if (channel.type == "SERIES") {
                val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
                val nextChannel = if (currIndex != -1 && currIndex < adjacentChannels.size - 1) adjacentChannels[currIndex + 1] else null
                if (nextChannel != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (showControls) 120.dp else 32.dp, end = 32.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.85f))
                                .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                .clickable { onChannelChange(nextChannel) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Próximo Episódio", color = Color.LightGray, fontSize = 12.sp)
                                Text(nextChannel.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Pular agora (${(timeLeftMs / 1000).toInt()}s)", color = NeonGreen, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = "Próximo", tint = NeonGreen)
                        }
                    }
                }
            } else if (channel.type == "MOVIE") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showControls) 120.dp else 32.dp, end = 32.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(12.dp))
                            .clickable { onBack() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Filme Terminando", color = Color.LightGray, fontSize = 12.sp)
                            Text("Voltar ao Menu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Sair agora (${(timeLeftMs / 1000).toInt()}s)", color = NeonGreen, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar", tint = NeonGreen)
                    }
                }
            }
        }"""

content = content.replace(target_next_popup, replace_next_popup)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

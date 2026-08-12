import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target = """                            CustomIPTVPlayer(
                                channel = channel,
                                adjacentChannels = viewModel.getAdjacentChannels(channel),
                                epgList = currentEPG,
                                initialPositionMs = initialPos,
                                bufferSize = viewModel.bufferSize.value,
                                onClose = { viewModel.selectChannel(null) },
                                onSaveProgress = { pos, dur -> viewModel.saveWatchProgress(channel, pos, dur) },
                                onChannelChange = { newCh -> viewModel.selectChannel(newCh) }
                            )"""

replace = """                            val isFav = favorites.any { it.streamId == channel.id && it.type == channel.type }
                            CustomIPTVPlayer(
                                channel = channel,
                                adjacentChannels = viewModel.getAdjacentChannels(channel),
                                epgList = currentEPG,
                                initialPositionMs = initialPos,
                                bufferSize = viewModel.bufferSize.value,
                                isFav = isFav,
                                onToggleFav = { viewModel.toggleFavorite(channel) },
                                onClose = { viewModel.selectChannel(null) },
                                onSaveProgress = { pos, dur -> viewModel.saveWatchProgress(channel, pos, dur) },
                                onChannelChange = { newCh -> viewModel.selectChannel(newCh) }
                            )"""

content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

target = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("full_screen_player_container")
    ) {"""

replace = """    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) {
        if (deviceLayoutMode == "TV") {
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("full_screen_player_container")
            .then(if (deviceLayoutMode == "TV") Modifier.focusRequester(focusRequester).focusable() else Modifier)
            .onKeyEvent { event ->
                if (deviceLayoutMode == "TV" && event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    val key = event.key
                    if (key == androidx.compose.ui.input.key.Key.DirectionCenter || key == androidx.compose.ui.input.key.Key.Enter || key == androidx.compose.ui.input.key.Key.NumPadEnter) {
                        showChannelsList = !showChannelsList
                        showControls = false
                        return@onKeyEvent true
                    } else if (showChannelsList && key == androidx.compose.ui.input.key.Key.DirectionRight) {
                        if (channel.type == "LIVE") {
                            showEPG = true
                            showChannelsList = false
                        }
                        return@onKeyEvent true
                    } else if (key == androidx.compose.ui.input.key.Key.Back) {
                        if (showChannelsList || showEPG) {
                            showChannelsList = false
                            showEPG = false
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }
    ) {"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

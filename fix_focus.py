with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Update LaunchedEffect to have a small delay
old_effect = """    LaunchedEffect(Unit) {
        if (deviceLayoutMode == "TV") {
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }"""
new_effect = """    LaunchedEffect(Unit) {
        if (deviceLayoutMode == "TV") {
            kotlinx.coroutines.delay(200)
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }"""
content = content.replace(old_effect, new_effect)

# And add .focusTarget() to the Box just in case
old_box = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("full_screen_player_container")
            .then(if (deviceLayoutMode == "TV") Modifier.focusRequester(focusRequester).focusable() else Modifier)"""
new_box = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("full_screen_player_container")
            .then(if (deviceLayoutMode == "TV") Modifier.focusRequester(focusRequester).focusable().androidx.compose.ui.focus.focusTarget() else Modifier)"""
content = content.replace(old_box, new_box)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

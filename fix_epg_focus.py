with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

old_lazycol = """                    val currentTime = System.currentTimeMillis()
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),"""

new_lazycol = """                    val currentTime = System.currentTimeMillis()
                    val epgFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
                    LaunchedEffect(showEPG) {
                        if (showEPG && deviceLayoutMode == "TV") {
                            try { epgFocusRequester.requestFocus() } catch (e: Exception) {}
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),"""

content = content.replace(old_lazycol, new_lazycol)

old_col = """                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()"""

new_col = """                            Column(
                                modifier = Modifier
                                    .then(if (isCurrent) Modifier.focusRequester(epgFocusRequester) else Modifier)
                                    .fillMaxWidth()"""

content = content.replace(old_col, new_col)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

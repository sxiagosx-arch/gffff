with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# Hide bottom channel/episodes button in TV mode
old_btn_series = """                            if (!inlineMode && (channel.type == "SERIES" || channel.type == "LIVE")) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    androidx.compose.material3.Button("""

new_btn_series = """                            if (!inlineMode && (channel.type == "SERIES" || channel.type == "LIVE") && deviceLayoutMode != "TV") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    androidx.compose.material3.Button("""

old_btn_live = """                            if (!inlineMode && channel.type == "LIVE") {
                                androidx.compose.material3.Button(
                                    onClick = { showChannelsList = !showChannelsList },"""

new_btn_live = """                            if (!inlineMode && channel.type == "LIVE" && deviceLayoutMode != "TV") {
                                androidx.compose.material3.Button(
                                    onClick = { showChannelsList = !showChannelsList },"""

content = content.replace(old_btn_series, new_btn_series)
content = content.replace(old_btn_live, new_btn_live)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

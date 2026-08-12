import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

# Update signature
target_sig = """    inlineMode: Boolean = false,
    onClose: () -> Unit,
    onSaveProgress: (Long, Long) -> Unit = { _, _ -> },
    onChannelChange: (IPTVChannel) -> Unit = {},
    onFullscreen: () -> Unit = {}
)"""
replace_sig = """    inlineMode: Boolean = false,
    isFav: Boolean = false,
    onToggleFav: () -> Unit = {},
    onClose: () -> Unit,
    onSaveProgress: (Long, Long) -> Unit = { _, _ -> },
    onChannelChange: (IPTVChannel) -> Unit = {},
    onFullscreen: () -> Unit = {}
)"""
content = content.replace(target_sig, replace_sig)

# Add Favorite button next to Mute button
target_mute = """                        // Right Top actions
                        Row {
                            IconButton(onClick = { isMuted = !isMuted }) {"""
replace_mute = """                        // Right Top actions
                        Row {
                            IconButton(onClick = onToggleFav) {
                                Icon(
                                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (isFav) Color.Red else Color.White
                                )
                            }
                            IconButton(onClick = { isMuted = !isMuted }) {"""
content = content.replace(target_mute, replace_mute)

# Also ensure Icons.Filled are imported if needed.
# (Usually they are, let's just make sure)
import_icons = "import androidx.compose.material.icons.filled.Favorite\nimport androidx.compose.material.icons.filled.FavoriteBorder\n"
if "Icons.Filled.Favorite" not in content and "Icons.Filled" not in content:
    content = content.replace("import androidx.compose.material.icons.rounded.*", import_icons + "import androidx.compose.material.icons.rounded.*")

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()
content = content.replace('.androidx.compose.ui.focus.focusProperties { exit = { androidx.compose.ui.focus.FocusRequester.Cancel } }', '')
# Fix the Box scope. Did I remove a Box { ?
# Looking at the original:
# AndroidView(...) { ... } was inside a Box?
# Let's check where `align` is used.
with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
    content = f.read()
if 'import androidx.compose.ui.focus.focusRequester' not in content:
    content = content.replace('import androidx.compose.ui.focus.onFocusChanged', 'import androidx.compose.ui.focus.onFocusChanged\nimport androidx.compose.ui.focus.focusRequester')
with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()
if 'import androidx.compose.material.icons.automirrored.rounded.List' not in content:
    content = content.replace('import androidx.compose.material.icons.rounded.*', 'import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.automirrored.rounded.List')
with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    content = f.read()
content = content.replace('val isTv = remember { DeviceUtil.isTv(context) }', 'val isTv: Boolean = remember { DeviceUtil.isTv(context) }')
with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)

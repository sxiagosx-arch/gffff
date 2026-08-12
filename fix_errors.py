with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()
if 'import androidx.compose.ui.focus.onFocusChanged' not in content:
    content = content.replace('import androidx.compose.ui.Alignment', 'import androidx.compose.ui.focus.onFocusChanged\nimport androidx.compose.ui.Alignment')
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()
# Fix focusProperties
content = content.replace('.androidx.compose.ui.focus.focusProperties { exit = { androidx.compose.ui.focus.FocusRequester.Cancel } }', '')
# Remove syntax error at end
if content.endswith('}'):
    pass # Wait, 'align' unresolved means the Box scope is broken.

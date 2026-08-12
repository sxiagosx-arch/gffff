with open('app/src/main/java/com/example/ui/screens/DeviceSelectionScreen.kt', 'r') as f:
    content = f.read()

import re
old_tv = """                // TV Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Charcoal)
                        .clickable { viewModel.setDeviceLayoutMode("TV") }
                        .padding(24.dp),"""
new_tv = """                // TV Option
                var isTvFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isTvFocused) NeonGreen.copy(alpha=0.3f) else Charcoal)
                        .border(if (isTvFocused) 3.dp else 0.dp, NeonGreen, RoundedCornerShape(16.dp))
                        .onFocusChanged { isTvFocused = it.isFocused }
                        .focusable()
                        .clickable { viewModel.setDeviceLayoutMode("TV") }
                        .padding(24.dp),"""
content = content.replace(old_tv, new_tv)

old_mobile = """                // Mobile Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Charcoal)
                        .clickable { viewModel.setDeviceLayoutMode("MOBILE") }
                        .padding(24.dp),"""
new_mobile = """                // Mobile Option
                var isMobileFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isMobileFocused) NeonGreen.copy(alpha=0.3f) else Charcoal)
                        .border(if (isMobileFocused) 3.dp else 0.dp, NeonGreen, RoundedCornerShape(16.dp))
                        .onFocusChanged { isMobileFocused = it.isFocused }
                        .focusable()
                        .clickable { viewModel.setDeviceLayoutMode("MOBILE") }
                        .padding(24.dp),"""
content = content.replace(old_mobile, new_mobile)

if "androidx.compose.ui.focus.onFocusChanged" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.focus.onFocusChanged\nimport androidx.compose.foundation.focusable")

with open('app/src/main/java/com/example/ui/screens/DeviceSelectionScreen.kt', 'w') as f:
    f.write(content)

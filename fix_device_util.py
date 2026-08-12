with open('app/src/main/java/com/example/util/DeviceUtil.kt', 'r') as f:
    content = f.read()

content = content.replace('}', '''
    fun isTv(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as android.app.UiModeManager
        return uiModeManager.currentModeType == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }
}''')

with open('app/src/main/java/com/example/util/DeviceUtil.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()
if 'import androidx.compose.ui.platform.LocalContext' not in content:
    content = content.replace('import androidx.compose.ui.platform.testTag', 'import androidx.compose.ui.platform.testTag\nimport androidx.compose.ui.platform.LocalContext')
content += "\n@androidx.compose.runtime.Composable\nfun MetadataLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {\n    androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {\n        androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Gray, modifier = androidx.compose.ui.Modifier.size(14.dp))\n        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(6.dp))\n        androidx.compose.material3.Text(text = text, color = androidx.compose.ui.graphics.Color.LightGray, fontSize = 12.sp)\n    }\n}\n"

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()
if 'import androidx.compose.foundation.focusable' not in content:
    content = content.replace('import androidx.compose.ui.focus.onFocusChanged', 'import androidx.compose.ui.focus.onFocusChanged\nimport androidx.compose.foundation.focusable')
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)


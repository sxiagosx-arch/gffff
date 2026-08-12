with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import re

old_sidebar_item = """@Composable
fun TvSidebarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) NeonGreenDim else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) NeonGreen else Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (selected) NeonGreen else Color.White,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}"""

new_sidebar_item = """@Composable
fun TvSidebarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) NeonGreen.copy(alpha = 0.3f) else if (selected) NeonGreenDim else Color.Transparent)
            .border(if (isFocused) 2.dp else 0.dp, if (isFocused) NeonGreen else Color.Transparent, RoundedCornerShape(8.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isFocused || selected) NeonGreen else Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (isFocused || selected) NeonGreen else Color.White,
            fontWeight = if (isFocused || selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}"""

content = content.replace(old_sidebar_item, new_sidebar_item)

# Remove parental control from TV Sidebar
content = re.sub(r'TvSidebarItem\(\s*icon = Icons\.Rounded\.FamilyRestroom,\s*label = "Controle Parental",\s*selected = currentScreen == Screen\.PARENTAL_CONTROL\s*\) \{ onNavigate\(Screen\.PARENTAL_CONTROL\) \}', '', content)
# Ensure Favorites is there
content = content.replace('import androidx.compose.ui.focus.onFocusChanged', 'import androidx.compose.ui.focus.onFocusChanged\nimport androidx.compose.foundation.focusable\nimport androidx.compose.foundation.border')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

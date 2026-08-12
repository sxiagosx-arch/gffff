with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target = """fun PhoneBottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Charcoal,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {"""

replace = """fun PhoneBottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = NeonGreen.copy(alpha = 0.15f),
        tonalElevation = 0.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(com.example.ui.theme.MatteBlack.copy(alpha = 0.6f))
    ) {"""

content = content.replace(target, replace)
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

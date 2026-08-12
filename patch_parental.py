with open('app/src/main/java/com/example/ui/screens/ParentalControlScreen.kt', 'r') as f:
    content = f.read()

target = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("parental_control_screen")
    ) {"""

replace = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .imePadding()
            .testTag("parental_control_screen")
    ) {"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/screens/ParentalControlScreen.kt', 'w') as f:
    f.write(content)

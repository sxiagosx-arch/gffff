with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target_route = "            Screen.SPLASH -> SplashScreen()"
replace_route = "            Screen.SPLASH -> SplashScreen()\n            Screen.DEVICE_SELECTION -> com.example.ui.screens.DeviceSelectionScreen(viewModel = viewModel)"

content = content.replace(target_route, replace_route)

# Now, we should use deviceLayoutMode instead of LocalConfiguration.current for isTvLandscape
target_tv = """                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val isTvLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE"""
replace_tv = """                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val deviceLayoutMode by viewModel.deviceLayoutMode.collectAsState()
                val isTvLandscape = if (deviceLayoutMode == "TV") true else if (deviceLayoutMode == "MOBILE") false else configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE"""
content = content.replace(target_tv, replace_tv)

target_nav = "val showNavigation = currentScreen != Screen.SPLASH && currentScreen != Screen.LOGIN"
replace_nav = "val showNavigation = currentScreen != Screen.SPLASH && currentScreen != Screen.LOGIN && currentScreen != Screen.DEVICE_SELECTION"
content = content.replace(target_nav, replace_nav)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

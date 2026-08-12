import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target = """@Composable
fun MainContentRouting(currentScreen: Screen, viewModel: IPTVViewModel) {
    when (currentScreen) {
        Screen.SPLASH -> SplashScreen()
        Screen.LOGIN -> LoginScreen(viewModel = viewModel)
        Screen.HOME -> MainDashboard(viewModel = viewModel)
        Screen.LIVE_TV -> LiveTVScreen(viewModel = viewModel)
        Screen.MOVIES -> MoviesScreen(viewModel = viewModel)
        Screen.SERIES -> SeriesScreen(viewModel = viewModel)
        Screen.PARENTAL_CONTROL -> ParentalControlScreen(viewModel = viewModel)
        Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
        Screen.FAVORITES -> FavoritesScreen(viewModel = viewModel)
        Screen.HISTORY -> HistoryScreen(viewModel = viewModel)
        Screen.ABOUT -> PlaceholderScreen("Sobre", "Neon IPTV Pro - Versão 1.0.0\\nCriado para a melhor experiência de IPTV.")
        else -> MainDashboard(viewModel = viewModel)
    }
}"""
replace = """@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun MainContentRouting(currentScreen: Screen, viewModel: IPTVViewModel) {
    androidx.compose.animation.AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) + 
            androidx.compose.animation.slideInVertically(animationSpec = androidx.compose.animation.core.tween(300)) { height -> height / 20 } togetherWith
            androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            Screen.SPLASH -> SplashScreen()
            Screen.LOGIN -> LoginScreen(viewModel = viewModel)
            Screen.HOME -> MainDashboard(viewModel = viewModel)
            Screen.LIVE_TV -> LiveTVScreen(viewModel = viewModel)
            Screen.MOVIES -> MoviesScreen(viewModel = viewModel)
            Screen.SERIES -> SeriesScreen(viewModel = viewModel)
            Screen.PARENTAL_CONTROL -> ParentalControlScreen(viewModel = viewModel)
            Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            Screen.FAVORITES -> FavoritesScreen(viewModel = viewModel)
            Screen.HISTORY -> HistoryScreen(viewModel = viewModel)
            Screen.ABOUT -> PlaceholderScreen("Sobre", "Neon IPTV Pro - Versão 1.0.0\\nCriado para a melhor experiência de IPTV.")
            else -> MainDashboard(viewModel = viewModel)
        }
    }
}"""

content = content.replace(target, replace)

if "import androidx.compose.animation.togetherWith" not in content:
    content = content.replace("import androidx.compose.animation.*", "import androidx.compose.animation.*\nimport androidx.compose.animation.togetherWith")

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

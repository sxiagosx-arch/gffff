with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

import re

# We will wrap MovieDetailsSheet in a Dialog so it traps focus!
old_sheet = """        // Animated Movie Detail Bottom Sheet Overlay
        AnimatedVisibility(
            visible = detailMovie != null,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            detailMovie?.let { movie ->
                val isFav = favorites.any { it.streamId == movie.id && it.type == "MOVIE" }
                MovieDetailsSheet(
                    movie = movie,
                    isFav = isFav,
                    onToggleFav = { viewModel.toggleFavorite(movie) },
                    onClose = { detailMovie = null }
                ) {
                    viewModel.selectChannel(movie)
                    detailMovie = null
                }
            }
        }"""

new_sheet = """        // Movie Detail Overlay
        if (detailMovie != null) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { detailMovie = null }, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
                detailMovie?.let { movie ->
                    val isFav = favorites.any { it.streamId == movie.id && it.type == "MOVIE" }
                    MovieDetailsSheet(
                        movie = movie,
                        isFav = isFav,
                        onToggleFav = { viewModel.toggleFavorite(movie) },
                        onClose = { detailMovie = null }
                    ) {
                        viewModel.selectChannel(movie)
                        detailMovie = null
                    }
                }
            }
        }"""

content = content.replace(old_sheet, new_sheet)


old_movie_sheet_fun = """fun MovieDetailsSheet(
    movie: IPTVChannel,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onClose: () -> Unit,
    onPlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(com.example.ui.theme.MatteBlack)
    ) {"""

new_movie_sheet_fun = """fun MovieDetailsSheet(
    movie: IPTVChannel,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onClose: () -> Unit,
    onPlay: () -> Unit
) {
    val isTv = com.example.util.DeviceUtil.isTv(LocalContext.current)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = if (isTv) Alignment.CenterEnd else Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isTv) 0.4f else 1f)
                .fillMaxHeight(if (isTv) 1f else 0.7f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = if (isTv) 0.dp else 24.dp, bottomStart = if (isTv) 24.dp else 0.dp))
                .background(com.example.ui.theme.MatteBlack)
        ) {"""

content = content.replace(old_movie_sheet_fun, new_movie_sheet_fun)
content = content.replace('Box(\n        modifier = Modifier\n            .fillMaxWidth()', 'Box(\n        modifier = Modifier')

# We need to make the button focusable and distinct on TV
btn_old = """            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {"""
btn_new = """            var isPlayFocused by remember { mutableStateOf(false) }
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = if (isPlayFocused) Color.White else NeonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .onFocusChanged { isPlayFocused = it.isFocused }
            ) {"""
content = content.replace(btn_old, btn_new)
content = content.replace('import androidx.compose.ui.platform.testTag', 'import androidx.compose.ui.platform.testTag\nimport androidx.compose.ui.focus.onFocusChanged')
content = content.replace('import androidx.compose.ui.platform.LocalFocusManager', 'import androidx.compose.ui.platform.LocalFocusManager\nimport androidx.compose.ui.platform.LocalContext')

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)

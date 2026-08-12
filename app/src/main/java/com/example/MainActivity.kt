package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ui.IPTVViewModel
import com.example.ui.Screen
import com.example.ui.player.CustomIPTVPlayer
import com.example.ui.screens.*
import com.example.ui.theme.Charcoal
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim
import com.example.worker.SyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // 1. FORÇAR MODO IMERSIVO (FULLSCREEN ABSOLUTO) SEMPRE QUE O APP TIVER FOCO
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val viewModel: com.example.ui.IPTVViewModel by viewModels()
        if (viewModel.selectedChannel.value != null) {
            val params = android.app.PictureInPictureParams.Builder()
                .setAspectRatio(android.util.Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemUI() // Oculta as barras assim que cria a tela

        com.example.ui.player.CronetUtil.init(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "IPTV_SYNC_WORK",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            MyApplicationTheme {
                val viewModel: IPTVViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val selectedChannel by viewModel.selectedChannel.collectAsState()
                val currentEPG by viewModel.currentEPG.collectAsState()
                val activeAccount by viewModel.activeAccount.collectAsState()
                val seriesSeasons by viewModel.seriesSeasons.collectAsState()
                val isLoadingApp by viewModel.isLoadingApp.collectAsState()

                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val deviceLayoutMode by viewModel.deviceLayoutMode.collectAsState()
                val isTvLandscape = if (deviceLayoutMode == "TV") true else if (deviceLayoutMode == "MOBILE") false else configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
                val showNavigation = currentScreen != Screen.SPLASH && currentScreen != Screen.LOGIN && currentScreen != Screen.DEVICE_SELECTION

                if (showNavigation) {
                    BackHandler(enabled = currentScreen != Screen.HOME) {
                        viewModel.navigateBack()
                    }
                }

                com.example.ui.components.PremiumBackground(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (!showNavigation) {
                        MainContentRouting(currentScreen = currentScreen, viewModel = viewModel)
                    } else {
                        // 2. RETIRADA DE TODOS OS INSETS PARA SUMIR A BARRA PRETA
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            containerColor = MatteBlack,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0) // <--- FORÇA O FIM DA BARRA PRETA AQUI
                        ) { innerPadding ->
                            // Sem o modifier de innerPadding, ele ignora as margens virtuais do sistema
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isTvLandscape) {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        TvSidebarPanel(
                                            currentScreen = currentScreen,
                                            activeAccountName = activeAccount?.name ?: "Sem Conta Ativa",
                                            onNavigate = { viewModel.navigateTo(it) }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                        ) {
                                            MainContentRouting(currentScreen = currentScreen, viewModel = viewModel)
                                        }
                                    }
                                } else {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            MainContentRouting(currentScreen = currentScreen, viewModel = viewModel)
                                        }
                                        PhoneBottomNavigationBar(
                                            currentScreen = currentScreen,
                                            onNavigate = { viewModel.navigateTo(it) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val watchHistory by viewModel.watchHistory.collectAsState(initial = emptyList())
                    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

                    AnimatedVisibility(
                        visible = selectedChannel != null,
                        enter = fadeIn() + expandIn(),
                        exit = fadeOut() + shrinkOut(),
                        modifier = Modifier.pointerInput(Unit) {}
                    ) {
                        selectedChannel?.let { channel ->
                            val hist = watchHistory.find { it.streamId == channel.id }
                            var initialPos = hist?.positionMs ?: 0L
                            val duration = hist?.durationMs ?: 0L
                            if (duration > 0 && initialPos >= duration * 0.95) {
                                initialPos = 0L
                            }
                            if (initialPos > 10000L && channel.type == "SERIES") {
                                initialPos -= 5000L
                            } else if (initialPos > 15000L && channel.type == "MOVIE") {
                                initialPos -= 10000L
                            }

                            val isFav = if (channel.type == "SERIES" && channel.seriesId.isNotEmpty()) {
                                favorites.any { it.streamId == channel.seriesId && it.type == "SERIES" }
                            } else {
                                favorites.any { it.streamId == channel.id && it.type == channel.type }
                            }
                            CustomIPTVPlayer(
                                channel = channel,
                                adjacentChannels = viewModel.getAdjacentChannels(channel),
                                seriesSeasons = seriesSeasons,
                                epgList = currentEPG,
                                initialPositionMs = initialPos,
                                bufferSize = viewModel.bufferSize.value,
                                isFav = isFav,
                                onToggleFav = {
                                    if (channel.type == "SERIES" && channel.seriesId.isNotEmpty()) {
                                        val series = viewModel.seriesList.value.find { it.id == channel.seriesId }
                                        if (series != null) viewModel.toggleFavoriteSeries(series)
                                    } else {
                                        viewModel.toggleFavorite(channel)
                                    }
                                },
                                onClose = { viewModel.selectChannel(null) },
                                onSaveProgress = { pos, dur -> viewModel.saveWatchProgress(channel, pos, dur) },
                                onChannelChange = { newCh -> viewModel.selectChannel(newCh) }
                            )
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isLoadingApp,
                    enter = androidx.compose.animation.EnterTransition.None,
                    exit = androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(400)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    com.example.ui.screens.SplashScreen()
                }
            }
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
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
            Screen.SPLASH -> Box(Modifier.fillMaxSize())
            Screen.DEVICE_SELECTION -> com.example.ui.screens.DeviceSelectionScreen(viewModel = viewModel)
            Screen.LOGIN -> LoginScreen(viewModel = viewModel)
            Screen.HOME -> MainDashboard(viewModel = viewModel)
            Screen.LIVE_TV -> LiveTVScreen(viewModel = viewModel)
            Screen.MOVIES -> MoviesScreen(viewModel = viewModel)
            Screen.SERIES -> SeriesScreen(viewModel = viewModel)
            Screen.PARENTAL_CONTROL -> ParentalControlScreen(viewModel = viewModel)
            Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            Screen.FAVORITES -> FavoritesScreen(viewModel = viewModel)
            Screen.HISTORY -> HistoryScreen(viewModel = viewModel)
            else -> MainDashboard(viewModel = viewModel)
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, message: String) {
    Box(modifier = Modifier.fillMaxSize().background(com.example.ui.theme.MatteBlack), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = com.example.ui.theme.NeonGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun TvSidebarPanel(
    currentScreen: Screen,
    activeAccountName: String,
    onNavigate: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(230.dp)
            .background(Charcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("U", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "UNLOCK TV",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "User",
                    tint = NeonGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("CONTA ATIVA", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = activeAccountName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 16.dp))

            TvSidebarItem(icon = Icons.Rounded.Home, label = "Início", selected = currentScreen == Screen.HOME) { onNavigate(Screen.HOME) }
            TvSidebarItem(icon = Icons.Rounded.LiveTv, label = "TV Ao Vivo", selected = currentScreen == Screen.LIVE_TV) { onNavigate(Screen.LIVE_TV) }
            TvSidebarItem(icon = Icons.Rounded.Movie, label = "Filmes", selected = currentScreen == Screen.MOVIES) { onNavigate(Screen.MOVIES) }
            TvSidebarItem(icon = Icons.Rounded.Tv, label = "Séries", selected = currentScreen == Screen.SERIES) { onNavigate(Screen.SERIES) }
            TvSidebarItem(icon = Icons.Rounded.Settings, label = "Configurações", selected = currentScreen == Screen.SETTINGS) { onNavigate(Screen.SETTINGS) }
            TvSidebarItem(icon = Icons.Rounded.Favorite, label = "Favoritos", selected = currentScreen == Screen.FAVORITES) { onNavigate(Screen.FAVORITES) }
        }
    }
}

@Composable
fun TvSidebarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
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
        Icon(imageVector = icon, contentDescription = label, tint = if (isFocused || selected) NeonGreen else Color.LightGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = if (isFocused || selected) NeonGreen else Color.White, fontWeight = if (isFocused || selected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
fun PhoneBottomNavigationBar(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    // 3. RETIRADA DA BARRA DO MENU INFERIOR
    NavigationBar(
        containerColor = NeonGreen.copy(alpha = 0.15f),
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0), // <--- FORÇA O FIM DA BARRA PRETA AQUI TBM
        modifier = Modifier
            .background(com.example.ui.theme.MatteBlack.copy(alpha = 0.6f))
    ) {
        val items = listOf(
            Triple(Screen.HOME, Icons.Rounded.Home, "Início"),
            Triple(Screen.LIVE_TV, Icons.Rounded.LiveTv, "TV"),
            Triple(Screen.MOVIES, Icons.Rounded.Movie, "Filmes"),
            Triple(Screen.SERIES, Icons.Rounded.Tv, "Séries"),
            Triple(Screen.SETTINGS, Icons.Rounded.Settings, "Ajustes"),
            Triple(Screen.FAVORITES, Icons.Rounded.Favorite, "Favoritos")
        )

        items.forEach { (screen, icon, label) ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = { Icon(imageVector = icon, contentDescription = label, tint = if (selected) Color.Black else Color.LightGray) },
                label = { Text(text = label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = NeonGreen, selectedTextColor = NeonGreen, unselectedTextColor = Color.LightGray)
            )
        }
    }
}
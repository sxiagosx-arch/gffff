package com.example.ui.player

import android.content.Context
import android.media.AudioManager
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.app.Activity
import android.os.Build


import androidx.compose.ui.Alignment
import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.model.IPTVChannel
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim
import com.example.ui.theme.NeonGreenGlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@kotlin.OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun CustomIPTVPlayer(
    channel: IPTVChannel,
    onClose: () -> Unit,
    onSaveProgress: (Long, Long) -> Unit,
    initialPositionMs: Long = 0L,
    adjacentChannels: List<IPTVChannel> = emptyList(),
    seriesSeasons: List<com.example.model.IPTVSeason> = emptyList(),
    epgList: List<com.example.model.EPGProgram> = emptyList(),
    bufferSize: String = "Médio (Padrão)",
    inlineMode: Boolean = false,
    isFav: Boolean = false,
    onToggleFav: () -> Unit = {},
    onChannelChange: (IPTVChannel) -> Unit,
    onFullscreen: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val activity = context as? Activity
    val config = LocalConfiguration.current
    val deviceLayoutMode = remember(config) {
        if ((config.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK) == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION) {
            "TV"
        } else if (config.screenWidthDp < 600) {
            "MOBILE"
        } else {
            "TABLET"
        }
    }
    
    // ExoPlayer State
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(true) }
    var playbackState by remember { mutableStateOf(Player.STATE_IDLE) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    
    // UI Controls
    var showControls by remember { mutableStateOf(false) }
    var playbackError by remember { mutableStateOf<String?>(null) }
    var showChannelsList by remember(channel.type) { mutableStateOf(channel.type == "LIVE" || channel.type == "SERIES") }
    // Gestures HUD States
    var gestureHUDText by remember { mutableStateOf("") }
    var gestureHUDProgress by remember { mutableStateOf(0f) } // 0 to 1
    var showGestureHUD by remember { mutableStateOf(false) }
    
    // Audio Manager
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat() }

    var isLocked by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var currentVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume) }

    val gestureDetector = remember {
        android.view.GestureDetector(context, object : android.view.GestureDetector.SimpleOnGestureListener() {
            var scrollYAccumulator = 0f
            override fun onDown(e: android.view.MotionEvent): Boolean {
                scrollYAccumulator = 0f
                return true
            }
            override fun onScroll(
                e1: android.view.MotionEvent?,
                e2: android.view.MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false
                val width = context.resources.displayMetrics.widthPixels
                val height = context.resources.displayMetrics.heightPixels
                
                if (abs(distanceY) > abs(distanceX)) {
                    // Vertical drag
                    if (e1.x < width / 2) {
                        // Brightness
                        showGestureHUD = true
                        val delta = distanceY / height
                        activity?.let { act ->
                            val attrs = act.window.attributes
                            val curBrightness = if (attrs.screenBrightness < 0) 0.5f else attrs.screenBrightness
                            val newBrightness = (curBrightness + delta).coerceIn(0.01f, 1.0f)
                            attrs.screenBrightness = newBrightness
                            act.window.attributes = attrs
                            
                            gestureHUDText = "Brilho: ${(newBrightness * 100).toInt()}%"
                            gestureHUDProgress = newBrightness
                        }
                    } else {
                        // Volume
                        showGestureHUD = true
                        scrollYAccumulator += distanceY
                        if (abs(scrollYAccumulator) > 40f) { // Threshold for volume tick
                            val direction = if (scrollYAccumulator > 0) android.media.AudioManager.ADJUST_LOWER else android.media.AudioManager.ADJUST_RAISE
                            audioManager.adjustStreamVolume(android.media.AudioManager.STREAM_MUSIC, direction, 0)
                            val newVol = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                            val maxVol = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                            currentVolume = newVol / maxVol
                            isMuted = currentVolume == 0f
                            gestureHUDProgress = currentVolume
                            gestureHUDText = "Volume: ${(gestureHUDProgress * 100).toInt()}%"
                            scrollYAccumulator = 0f
                        }
                    }
                } else {
                    // Seek
                    if (totalDuration > 0) {
                        showGestureHUD = true
                        val seekDelta = (-distanceX / width * totalDuration).toLong()
                        exoPlayer?.let { player ->
                            val targetPos = (player.currentPosition + seekDelta).coerceIn(0, totalDuration)
                            player.seekTo(targetPos)
                            currentPosition = targetPos
                            
                            gestureHUDText = "Procurar: ${formatTime(targetPos)} / ${formatTime(totalDuration)}"
                            gestureHUDProgress = targetPos.toFloat() / totalDuration
                        }
                    }
                }
                return true
            }
            
            override fun onSingleTapConfirmed(e: android.view.MotionEvent): Boolean {
                showControls = !showControls
                return true
            }
        })
    }
    var scaleMode by remember { mutableIntStateOf(if (deviceLayoutMode == "TV") androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL else androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var isFullscreen by remember { mutableStateOf(true) }
    var showAutoPlayCountdown by remember { mutableStateOf(false) }
    var dismissNextPopup by remember { mutableStateOf(false) }

    LaunchedEffect(channel.id) {
        dismissNextPopup = false
        showChannelsList = false
    }
    var autoPlayTimeLeft by remember { mutableIntStateOf(10) }
    var nextEpisode by remember { mutableStateOf<com.example.model.IPTVChannel?>(null) }
    var showTechInfo by remember { mutableStateOf(false) }

    val isInPiP = (context as? Activity)?.isInPictureInPictureMode ?: false

    
    var showCastDialog by remember { mutableStateOf(false) }

    
    // Coroutine Scope
    val scope = rememberCoroutineScope()

    // Keep screen on and force landscape
    DisposableEffect(inlineMode) {
        val originalOrientation = activity?.requestedOrientation
        if (!inlineMode) {
            activity?.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (activity != null) {
                val controller = androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            if (!inlineMode) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                if (activity != null) {
                    val controller = androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                    controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                }
                if (originalOrientation != null) {
                    activity.requestedOrientation = originalOrientation
                }
            }
        }
    }
    // Initialize Player
    DisposableEffect(context) {
        val observer = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                val newVol = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat() / maxVolume
                if (kotlin.math.abs(newVol - currentVolume) > 0.01f) {
                    currentVolume = newVol
                    isMuted = newVol == 0f
                }
            }
        }
        context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI, true, observer
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }
    
    LaunchedEffect(isFullscreen) {
        if (activity != null && !inlineMode) {
            val controller = androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            if (isFullscreen) {
                controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            } else {
                controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Create and Manage Player Lifecycle
    DisposableEffect(channel, bufferSize) {
        val (minBuf, maxBuf, playReady, playResume) = when (bufferSize) {
            "Grande (Reduz engasgos)" -> listOf(15000, 30000, 1000, 2000)
            "Pequeno (Troca rápida)" -> listOf(2000, 5000, 100, 500)
            else -> listOf(5000, 10000, 250, 500) // Super fast start for high bandwidth
        }
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(minBuf, maxBuf, playReady, playResume)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()



            
        val dataSourceFactory = CronetUtil.getDataSourceFactory()
            
        val player = ExoPlayerManager.getPlayer(context, loadControl, dataSourceFactory, channel, initialPositionMs)
        
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                playbackState = state
                if (state == Player.STATE_READY) {
                    totalDuration = player.duration
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                playbackError = "Erro ao reproduzir vídeo. Tente novamente mais tarde."
            }
        }
        player.addListener(listener)
        
        exoPlayer = player
        
        onDispose {
            player.removeListener(listener)
            ExoPlayerManager.releasePlayer(player)
            if (exoPlayer == player) {
                exoPlayer = null
            }
        }
    }

    // Auto-Play Logic
    LaunchedEffect(playbackState) {
        if (playbackState == androidx.media3.common.Player.STATE_ENDED && channel.type == "SERIES") {
            val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
            if (currIndex != -1 && currIndex < adjacentChannels.size - 1) {
                nextEpisode = adjacentChannels[currIndex + 1]
                showAutoPlayCountdown = true
                autoPlayTimeLeft = 10
            }
        } else {
            showAutoPlayCountdown = false
        }
    }

    LaunchedEffect(showAutoPlayCountdown) {
        if (showAutoPlayCountdown) {
            while (autoPlayTimeLeft > 0) {
                kotlinx.coroutines.delay(1000)
                autoPlayTimeLeft -= 1
            }
            if (autoPlayTimeLeft == 0 && nextEpisode != null) {
                showAutoPlayCountdown = false
                onChannelChange(nextEpisode!!)
            }
        }
    }

    // Periodic Progress Tracker
    LaunchedEffect(exoPlayer, isPlaying) {
        while (exoPlayer != null && isPlaying) {
            delay(1000)
            exoPlayer?.let {
                currentPosition = it.currentPosition
                totalDuration = it.duration
                if (totalDuration > 0) {
                    onSaveProgress(currentPosition, totalDuration)
                }
            }
        }
    }

    // Auto Hide Controls (Only if Channels List is not open)
    LaunchedEffect(showControls, showChannelsList) {
        if (showControls && !isLocked && !showChannelsList) {
            delay(5000)
            showControls = false
        }
    }

    BackHandler {
        if (isLocked) {
            // Shake or show unlock tip
        } else {
            onClose()
        }
    }

    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) {
        if (deviceLayoutMode == "TV") {
            kotlinx.coroutines.delay(200)
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("full_screen_player_container")
            .then(if (deviceLayoutMode == "TV") Modifier.focusRequester(focusRequester).focusable() else Modifier)
            .onKeyEvent { event ->
                if (deviceLayoutMode == "TV" && event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp) {
                    val keyCode = (event.nativeKeyEvent as android.view.KeyEvent).keyCode
                    if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (showChannelsList) return@onKeyEvent false
                        
                        if (channel.type == "LIVE") {
                            showChannelsList = true
                            showControls = false
                        } else {
                            exoPlayer?.let { player ->
                                if (player.isPlaying) player.pause() else player.play()
                            }
                            showControls = true
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (channel.type != "LIVE") {
                            exoPlayer?.let { player ->
                                val newPos = (player.currentPosition + 15000).coerceAtMost(player.duration)
                                player.seekTo(newPos)
                                showControls = true
                            }
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (channel.type != "LIVE") {
                            exoPlayer?.let { player ->
                                val newPos = (player.currentPosition - 15000).coerceAtLeast(0)
                                player.seekTo(newPos)
                                showControls = true
                            }
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP) {
                        if (channel.type == "LIVE") {
                            val index = adjacentChannels.indexOfFirst { it.id == channel.id }
                            if (index > 0) {
                                onChannelChange(adjacentChannels[index - 1])
                                try { focusRequester.requestFocus() } catch (e: Exception) {}
                            }
                        } else {
                            showChannelsList = true
                            showControls = false
                        }
                        return@onKeyEvent true
                    } else if (!showChannelsList && keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (channel.type == "LIVE") {
                            val index = adjacentChannels.indexOfFirst { it.id == channel.id }
                            if (index != -1 && index < adjacentChannels.size - 1) {
                                onChannelChange(adjacentChannels[index + 1])
                                try { focusRequester.requestFocus() } catch (e: Exception) {}
                            }
                        } else {
                            showChannelsList = true
                            showControls = false
                        }
                        return@onKeyEvent true
                    } else if (keyCode == android.view.KeyEvent.KEYCODE_BACK || keyCode == android.view.KeyEvent.KEYCODE_ESCAPE) {
                        if (showChannelsList || showControls) {
                            showChannelsList = false
                            showControls = false
                            try { focusRequester.requestFocus() } catch (e: Exception) {}
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }
    ) {
        // Player Surface View
        AndroidView(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (showChannelsList) {
                            showChannelsList = false
                        } else {
                            showControls = !showControls
                        }
                        focusManager.clearFocus()
                    }
                )
            },
            factory = { ctx ->
                androidx.media3.ui.PlayerView(ctx).apply {
                    useController = false
                    isFocusable = false
                    isFocusableInTouchMode = false
                    descendantFocusability = android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    resizeMode = scaleMode
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
                playerView.resizeMode = scaleMode
            }
        )

        if (playbackError != null) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = playbackError ?: "", color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        playbackError = null
                        exoPlayer?.prepare()
                        exoPlayer?.play()
                    }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) {
                        Text("Tentar Novamente", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (channel.url.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Text(text = "URL do vídeo indisponível.", color = Color.White)
            }
        }
        

        
        if (playbackState == Player.STATE_BUFFERING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeonGreen, strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Buffer Inteligente...",
                        color = NeonGreen,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Gesture Overlay HUD
        AnimatedVisibility(
            visible = showGestureHUD,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = gestureHUDText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { gestureHUDProgress },
                        modifier = Modifier
                            .width(150.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonGreen,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // Player Controls (Neon Matte Overlay)
        AnimatedVisibility(
            visible = showControls && ((LocalContext.current as? Activity)?.isInPictureInPictureMode != true) && !(showChannelsList && channel.type == "SERIES"),
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut() + slideOutVertically { -it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                if (isLocked) {
                    // Lock-only controls (Allow Unlock)
                    IconButton(
                        onClick = { isLocked = false },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(24.dp)
                            .size(54.dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LockOpen,
                            contentDescription = "Desbloquear",
                            tint = NeonGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    // Full Control panel

                    // TOP BAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            if (deviceLayoutMode != "TV") {
                                IconButton(
                                    onClick = onClose,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = "Voltar",
                                        tint = NeonGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = channel.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                if (channel.epgTitle.isNotEmpty()) {
                                    Text(
                                        text = channel.epgTitle,
                                        color = NeonGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        // Right Top actions
                        if (deviceLayoutMode != "TV") {
                        Row {
                            val localContext = androidx.compose.ui.platform.LocalContext.current
                            IconButton(onClick = { showCastDialog = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Cast,
                                    contentDescription = "Transmitir",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = onToggleFav) {
                                Icon(
                                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (isFav) Color.Red else Color.White
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { 
                                    if (currentVolume == 0f) {
                                        currentVolume = 1f
                                        isMuted = false
                                    } else {
                                        isMuted = !isMuted
                                    }
                                    val targetVol = if (isMuted) 0 else (currentVolume * maxVolume).toInt()
                                    audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVol, 0)
                                }) {
                                    Icon(
                                        imageVector = if (isMuted || currentVolume == 0f) Icons.Rounded.VolumeOff else if (currentVolume < 0.5f) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp,
                                        contentDescription = "Mudo",
                                        tint = if (isMuted || currentVolume == 0f) Color.White else NeonGreen
                                    )
                                }
                                
                                Slider(
                                    value = if (isMuted) 0f else currentVolume,
                                    onValueChange = { vol ->
                                        currentVolume = vol
                                        isMuted = vol == 0f
                                        val targetVol = if (isMuted) 0 else (currentVolume * maxVolume).toInt()
                                        audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVol, 0)
                                    },
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonGreen,
                                        activeTrackColor = NeonGreen,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                                        activeTickColor = Color.Transparent,
                                        inactiveTickColor = Color.Transparent
                                    ),
                                    modifier = Modifier.width(80.dp).height(24.dp).padding(end = 8.dp)
                                )
                            }

                            IconButton(onClick = { 
                                scaleMode = when (scaleMode) {
                                    androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                                    androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                    else -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                                }
                            }) {
                                Icon(
                                    imageVector = when (scaleMode) {
                                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT -> Icons.Rounded.Fullscreen
                                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL -> Icons.Rounded.OpenInFull
                                        else -> Icons.Rounded.ZoomIn
                                    },
                                    contentDescription = "Aspect Ratio",
                                    tint = if (scaleMode != androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT) NeonGreen else Color.White
                                )
                            }


                            
                            IconButton(onClick = { isLocked = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Bloquear Tela",
                                    tint = Color.White
                                )
                            }
                        }
                        }
                    }

                    // CENTER PLAYBACK / CHANNELS SWITCHERS
                    if (deviceLayoutMode != "TV") {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Previous Button (Channel or -15s)
                        if (channel.type == "LIVE") {
                            if (adjacentChannels.isNotEmpty()) {
                                val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
                                val prevChannel = if (currIndex > 0) adjacentChannels[currIndex - 1] else null
                                IconButton(
                                    onClick = { prevChannel?.let { onChannelChange(it) } },
                                    enabled = prevChannel != null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(27.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SkipPrevious,
                                        contentDescription = "Canal Anterior",
                                        tint = if (prevChannel != null) Color.White else Color.Gray,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { 
                                    exoPlayer?.let {
                                        val newPos = (it.currentPosition - 15000L).coerceAtLeast(0L)
                                        it.seekTo(newPos)
                                        currentPosition = newPos
                                    }
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(27.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FastRewind,
                                    contentDescription = "Voltar 15s",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        // Main Play Pause
                        IconButton(
                            onClick = {
                                exoPlayer?.let {
                                    if (isPlaying) it.pause() else it.play()
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(36.dp))
                                .background(NeonGreen)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                                tint = Color.Black,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        // Next Button (Channel or +15s)
                        if (channel.type == "LIVE") {
                            if (adjacentChannels.isNotEmpty()) {
                                val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
                                val nextChannel = if (currIndex != -1 && currIndex < adjacentChannels.size - 1) adjacentChannels[currIndex + 1] else null
                                IconButton(
                                    onClick = { nextChannel?.let { onChannelChange(it) } },
                                    enabled = nextChannel != null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(27.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SkipNext,
                                        contentDescription = "Próximo Canal",
                                        tint = if (nextChannel != null) Color.White else Color.Gray,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { 
                                    exoPlayer?.let {
                                        val newPos = (it.currentPosition + 15000L).coerceAtMost(it.duration.coerceAtLeast(0L))
                                        it.seekTo(newPos)
                                        currentPosition = newPos
                                    }
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(27.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FastForward,
                                    contentDescription = "Avançar 15s",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    }

                    // BOTTOM SEEKBAR / TIME PROGRESS
                    if (totalDuration > 0) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp)
                        ) {
                            if (!inlineMode && (channel.type == "SERIES" || channel.type == "LIVE") && deviceLayoutMode != "TV") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    androidx.compose.material3.Button(
                                        onClick = { 
                                            showChannelsList = !showChannelsList 
                                            focusManager.clearFocus()
                                        },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Rounded.List, contentDescription = if (channel.type == "SERIES") "Episódios" else "Canais", tint = NeonGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (channel.type == "SERIES") "Episódios" else "Canais", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatTime(currentPosition),
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = formatTime(totalDuration),
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (deviceLayoutMode == "TV") {
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f,
                                    color = NeonGreen,
                                    trackColor = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.fillMaxWidth().height(4.dp)
                                )
                            } else {
                                Slider(
                                    value = currentPosition.toFloat(),
                                    onValueChange = { pos ->
                                        currentPosition = pos.toLong()
                                        exoPlayer?.seekTo(currentPosition)
                                    },
                                    valueRange = 0f..totalDuration.toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonGreen,
                                        activeTrackColor = NeonGreen,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        // Live indicator at bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            if (!inlineMode && channel.type == "LIVE" && deviceLayoutMode != "TV") {
                                androidx.compose.material3.Button(
                                    onClick = { showChannelsList = !showChannelsList },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(imageVector = Icons.Rounded.List, contentDescription = "Canais", tint = NeonGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Canais", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AO VIVO",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        }
                    }
                    
                    // Fullscreen Button for Inline Mode
                    if (inlineMode) {
                        IconButton(
                            onClick = onFullscreen,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Fullscreen,
                                contentDescription = "Tela Cheia",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Channels List Panel on the Left (LIVE TV)
        AnimatedVisibility(
            visible = !isLocked && !inlineMode && showChannelsList && adjacentChannels.isNotEmpty() && channel.type != "SERIES",
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(2.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Canais",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        androidx.compose.material3.Button(
                            onClick = { 
                                showChannelsList = false 
                                focusManager.clearFocus()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .testTag("close_episodes_list")
                                .focusable(true)
                        ) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Fechar", tint = Color.White)
                        }
                    }
                    val channelListFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
                    LaunchedEffect(showChannelsList) {
                        if (showChannelsList && deviceLayoutMode == "TV") {
                            try { channelListFocusRequester.requestFocus() } catch (e: Exception) {}
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(adjacentChannels) { ch ->
                            val isSelected = ch.id == channel.id
                            var isFocused by remember { mutableStateOf(false) }
                            Row(
                                modifier = Modifier
                                    .then(if (isSelected) Modifier.focusRequester(channelListFocusRequester) else Modifier)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isFocused) NeonGreen.copy(alpha=0.5f) else if (isSelected) NeonGreen else Color.Black)
                                    .border(if (isFocused) 3.dp else 1.dp, NeonGreen, RoundedCornerShape(12.dp))
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                    .onKeyEvent { event ->
                                        if (event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp && 
                                            (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                                             event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                                            onChannelChange(ch)
                                            showChannelsList = false
                                            if (deviceLayoutMode == "TV") {
                                                try { focusRequester.requestFocus() } catch (e: Exception) {}
                                            }
                                            true
                                        } else false
                                    }
                                    .clickable { 
                                        onChannelChange(ch)
                                        showChannelsList = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ch.name,
                                    color = if (isSelected || isFocused) Color.Black else NeonGreen,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Episodes List Panel on the Bottom (SERIES)
        AnimatedVisibility(
            visible = !isLocked && !inlineMode && showChannelsList && (adjacentChannels.isNotEmpty() || seriesSeasons.isNotEmpty()) && channel.type == "SERIES",
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val episodeListFocusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
                    LaunchedEffect(showChannelsList) {
                        if (showChannelsList && deviceLayoutMode == "TV") {
                            try { episodeListFocusRequester.requestFocus() } catch (e: Exception) {}
                        }
                    }
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (seriesSeasons.isNotEmpty()) {
                            seriesSeasons.forEach { season ->
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 4.dp).height(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ViewList,
                                            contentDescription = "Temporada",
                                            tint = NeonGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "T${season.number}",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                items(season.episodes) { ch ->
                                    val isSelected = ch.id == channel.id
                                    val epNumMatch = Regex("(?i)(?:E|EP|Episódio|Episode)\\s*(\\d+)").find(ch.name)
                                    val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                                    var isFocused by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .then(if (isSelected) Modifier.focusRequester(episodeListFocusRequester) else Modifier)
                                            .width(64.dp)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) NeonGreen else if (isFocused) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.65f))
                                            .border(if (isFocused) 2.dp else 1.dp, if (isFocused) Color.White else NeonGreen, RoundedCornerShape(8.dp))
                                            .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                            .onKeyEvent { event ->
                                                if (event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp && 
                                                    (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                                                     event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                                                    onChannelChange(ch)
                                                    showChannelsList = false
                                                    if (deviceLayoutMode == "TV") {
                                                        try { focusRequester.requestFocus() } catch (e: Exception) {}
                                                    }
                                                    true
                                                } else false
                                            }
                                            .focusable()
                                            .clickable { 
                                                onChannelChange(ch)
                                                showChannelsList = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayNum,
                                            color = if (isSelected) Color.Black else if (isFocused) Color.White else NeonGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                        } else {
                            items(adjacentChannels) { ch ->
                                val isSelected = ch.id == channel.id
                                val epNumMatch = Regex("(?i)(?:E|EP|Episódio|Episode)\\s*(\\d+)").find(ch.name)
                                val displayNum = epNumMatch?.groupValues?.get(1) ?: ch.name.take(10)
                                
                                var isFocused by remember { mutableStateOf(false) }
                                Box(
                                    modifier = Modifier
                                        .then(if (isSelected) Modifier.focusRequester(episodeListFocusRequester) else Modifier)
                                        .width(64.dp)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonGreen else if (isFocused) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.65f))
                                        .border(if (isFocused) 2.dp else 1.dp, if (isFocused) Color.White else NeonGreen, RoundedCornerShape(8.dp))
                                        .onFocusChanged { isFocused = it.isFocused }
                                    .focusable()
                                        .onKeyEvent { event ->
                                            if (event.type == androidx.compose.ui.input.key.KeyEventType.KeyUp && 
                                                (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER || 
                                                 event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                                                onChannelChange(ch)
                                                showChannelsList = false
                                                if (deviceLayoutMode == "TV") {
                                                    try { focusRequester.requestFocus() } catch (e: Exception) {}
                                                }
                                                true
                                            } else false
                                        }
                                        .focusable()
                                        .clickable { 
                                            onChannelChange(ch)
                                            showChannelsList = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = displayNum,
                                        color = if (isSelected) Color.Black else NeonGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Next Episode Popup
        val timeLeftMs = totalDuration - currentPosition
        if (!dismissNextPopup && channel.type != "LIVE" && totalDuration > 0 && timeLeftMs in 1L..240000L) {
            val currIndex = adjacentChannels.indexOfFirst { it.id == channel.id }
            val nextChannel = if (currIndex != -1 && currIndex < adjacentChannels.size - 1) adjacentChannels[currIndex + 1] else null
            if (nextChannel != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showControls) 120.dp else 32.dp, end = 32.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                            .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.clickable { onChannelChange(nextChannel) }) {
                            Text(if (channel.type == "SERIES") "Próximo Episódio" else "Próximo Filme", color = Color.Gray, fontSize = 9.sp)
                            Text(nextChannel.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, modifier = Modifier.widthIn(max = 140.dp))
                            Text("Pular (${(timeLeftMs / 1000).toInt()}s)", color = NeonGreen, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = "Próximo", tint = NeonGreen, modifier = Modifier.size(20.dp).clickable { onChannelChange(nextChannel) })
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { dismissNextPopup = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Fechar", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Tech Info Panel Overlay (Floating neon glass card)
        // Auto-Play Countdown Overlay
        AnimatedVisibility(
            visible = showAutoPlayCountdown,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Próximo Episódio",
                        color = Color.LightGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = nextEpisode?.name ?: "",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.widthIn(max = 400.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Iniciando em $autoPlayTimeLeft segundos...",
                        color = NeonGreen,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        androidx.compose.material3.Button(
                            onClick = { showAutoPlayCountdown = false },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Cancelar", color = Color.White)
                        }
                        androidx.compose.material3.Button(
                            onClick = { 
                                showAutoPlayCountdown = false
                                if (nextEpisode != null) {
                                    onChannelChange(nextEpisode!!)
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NeonGreen)
                        ) {
                            Text("Assistir Agora", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showTechInfo && !isLocked,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 20.dp)
                .width(280.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .background(NeonGreenDim)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "DETALHES TÉCNICOS",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Divider(color = NeonGreen.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    
                    TechRow(label = "Codec", value = "H.264 / AAC")
                    TechRow(label = "Resolução", value = if (channel.resolution.isNotEmpty()) channel.resolution else "1080p (Full HD)")
                    TechRow(label = "Bitrate", value = "4.2 Mbps")
                    TechRow(label = "Framerate", value = "60 FPS")
                    TechRow(label = "Latência", value = "1.15s")
                    TechRow(label = "Buffer", value = "${(totalDuration / 1000).coerceAtMost(5)}s")
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estabilidade", color = Color.LightGray, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonGreenGlow)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Excelente",
                                color = NeonGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        if (showCastDialog) {
            com.example.ui.components.CastDialog { showCastDialog = false }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TechRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

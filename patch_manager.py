import re

with open('/app/applet/app/src/main/java/com/example/ui/player/ExoPlayerManager.kt', 'r') as f:
    content = f.read()

target = """    fun getPlayer(
        context: Context,
        loadControl: LoadControl,
        dataSourceFactory: DefaultHttpDataSource.Factory,
        channel: IPTVChannel,
        initialPositionMs: Long
    ): ExoPlayer {"""
replace = """    fun getPlayer(
        context: Context,
        loadControl: LoadControl,
        dataSourceFactory: DefaultHttpDataSource.Factory,
        channel: IPTVChannel,
        initialPositionMs: Long,
        streamQuality: String = "Automática"
    ): ExoPlayer {"""

content = content.replace(target, replace)

target_builder = """        val player = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {"""

replace_builder = """        val trackSelector = androidx.media3.exoplayer.trackselection.DefaultTrackSelector(context)
        val paramsBuilder = trackSelector.buildUponParameters()
        when (streamQuality) {
            "Alta (1080p)" -> paramsBuilder.setMaxVideoSize(1920, 1080)
            "Média (720p)" -> paramsBuilder.setMaxVideoSize(1280, 720)
            "Baixa (480p)" -> paramsBuilder.setMaxVideoSize(854, 480)
            else -> paramsBuilder.clearVideoSizeConstraints()
        }
        trackSelector.setParameters(paramsBuilder)

        val player = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {"""
            
content = content.replace(target_builder, replace_builder)

with open('/app/applet/app/src/main/java/com/example/ui/player/ExoPlayerManager.kt', 'w') as f:
    f.write(content)

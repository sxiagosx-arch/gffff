package com.example.ui.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.datasource.DataSource
import com.example.model.IPTVChannel

object ExoPlayerManager {
    var currentPlayer: ExoPlayer? = null
    var currentChannelUrl: String? = null
    private var refCount = 0

    fun getPlayer(
        context: Context,
        loadControl: LoadControl,
        dataSourceFactory: androidx.media3.datasource.DataSource.Factory,
        channel: IPTVChannel,
        initialPositionMs: Long
    ): ExoPlayer {
        refCount++
        if (currentPlayer != null && currentChannelUrl == channel.url && currentPlayer!!.playerError == null) {
            return currentPlayer!!
        }
        currentPlayer?.release()

        val trackSelector = DefaultTrackSelector(context)
        val bandwidthMeter = DefaultBandwidthMeter.getSingletonInstance(context)
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            .setEnableDecoderFallback(true)

        val player = ExoPlayer.Builder(context, renderersFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
                
                // Optimizando buffering e inicio rapido de reproduccion
                videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT

                if (channel.url.isNotEmpty()) {
                    setMediaItem(MediaItem.fromUri(channel.url))
                    if (initialPositionMs > 0L) {
                        seekTo(initialPositionMs)
                    }
                    prepare()
                }
            }
        currentPlayer = player
        currentChannelUrl = channel.url
        return player
    }

    fun releasePlayer(player: ExoPlayer?) {
        if (player == null) return
        refCount--
        if (refCount <= 0) {
            if (currentPlayer === player) {
                currentPlayer?.release()
                currentPlayer = null
                currentChannelUrl = null
            }
            refCount = 0
        }
    }
}

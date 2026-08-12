package com.example.ui.player

import android.content.Context
import android.util.Log
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import com.google.android.gms.net.CronetProviderInstaller
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

object CronetUtil {
    private var cronetEngine: CronetEngine? = null
    private val executor = Executors.newSingleThreadExecutor()
    private var isProviderInstalled = false

    fun init(context: Context) {
        if (isProviderInstalled) return
        CronetProviderInstaller.installProvider(context.applicationContext).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isProviderInstalled = true
                try {
                    cronetEngine = CronetEngine.Builder(context.applicationContext)
                        .enableHttp2(true)
                        .enableQuic(true)
                        .build()
                } catch (e: Exception) {
                    Log.e("CronetUtil", "Failed to build CronetEngine", e)
                }
            } else {
                Log.e("CronetUtil", "Failed to install Cronet Provider", task.exception)
            }
        }
    }

    fun getDataSourceFactory(): DataSource.Factory {
        val engine = cronetEngine
        return if (engine != null) {
            CronetDataSource.Factory(engine, executor)
        } else {
            DefaultHttpDataSource.Factory()
                .setUserAgent("VLC/3.0.0")
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setAllowCrossProtocolRedirects(true)
        }
    }
}

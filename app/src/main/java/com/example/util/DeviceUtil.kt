package com.example.util

import android.content.Context
import java.util.UUID

object DeviceUtil {
    private const val PREFS_NAME = "device_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            val randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 12).uppercase()
            deviceId = "${android.os.Build.MANUFACTURER.take(3).uppercase()}-$randomStr"
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId!!
    }

    fun isTv(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as android.app.UiModeManager
        return uiModeManager.currentModeType == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }
}

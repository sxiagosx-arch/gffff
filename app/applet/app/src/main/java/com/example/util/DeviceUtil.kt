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
}

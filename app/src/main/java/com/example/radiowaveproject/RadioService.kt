package com.example.radiowaveproject

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class RadioService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val frequency = intent.getFloatExtra(EXTRA_FREQUENCY, 89.1f)
                val isFM = intent.getBooleanExtra(EXTRA_IS_FM, true)
                Log.d("RadioService", "Starting radio: ${if (isFM) "FM" else "AM"} at $frequency MHz")
                // Call the actual radio hardware here
            }

            ACTION_STOP -> {
                Log.d("RadioService", "Stopping radio")
                // Stop the hardware radio playback
            }
        }
        return START_NOT_STICKY
    }

    companion object {
        const val ACTION_START = "com.example.radiowaveproject.ACTION_START"
        const val ACTION_STOP = "com.example.radiowaveproject.ACTION_STOP"
        const val EXTRA_FREQUENCY = "EXTRA_FREQUENCY"
        const val EXTRA_IS_FM = "EXTRA_IS_FM"
    }
}

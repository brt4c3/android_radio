package com.example.radiowaveproject.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.example.radiowaveproject.RadioConstants.LOG_BROADCAST

@Composable
fun LogReceiver(logs: MutableList<String>) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.getStringExtra("log_message")?.let { logs.add("🔊 $it") }
            }
        }
        val filter = IntentFilter(LOG_BROADCAST)
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}

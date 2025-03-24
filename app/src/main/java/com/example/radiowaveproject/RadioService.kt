package com.example.radiowaveproject

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress

class RadioService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "radio_service_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Handles start/stop commands sent via intents.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            RadioConstants.ACTION_START -> {
                val stationName = intent.getStringExtra(RadioConstants.EXTRA_STATION_NAME)
                val streamUrl = RadioConstants.radioStations[stationName]

                // Create a foreground notification for Android O+.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForeground(1, createNotification(stationName))
                }

                if (streamUrl != null) {
                    log("Selected station: $stationName")
                    CoroutineScope(Dispatchers.IO).launch {
                        // Check network connectivity and host reachability
                        val internetOk = ping("8.8.8.8")
                        val hostOk = ping(getHostFromUrl(streamUrl))
                        if (internetOk && hostOk) {
                            val latency = measureLatency(getHostFromUrl(streamUrl))
                            val bgpInfo = resolveDns(getHostFromUrl(streamUrl))
                            log("Latency to stream: ${latency}ms")
                            log("DNS BGP Info: $bgpInfo")
                            playStream(streamUrl)
                        } else {
                            log("⚠️ Network check failed. Cannot stream.")
                        }
                    }
                } else {
                    log("Stream URL not found for station: $stationName")
                }
            }

            RadioConstants.ACTION_STOP -> {
                log("Stopping radio service.")
                stopStream()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    /**
     * Creates a notification for running the service in the foreground.
     */
    private fun createNotification(stationName: String?): Notification {
        val channelName = "Radio Service"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Radio Streaming")
            .setContentText("Playing: ${stationName ?: "Unknown Station"}")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }

    /**
     * Starts the MediaPlayer to play the given stream URL.
     */
    private fun playStream(url: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    log("Buffering complete. Playing now.")
                    it.start()
                }
                setOnErrorListener { _, what, extra ->
                    log("MediaPlayer error: what=$what, extra=$extra")
                    stopSelf()
                    true
                }
                log("Buffering stream: $url")
                prepareAsync()
            }
        } catch (e: Exception) {
            log("❌ Playback failed: ${e.message}")
            Log.e("RadioService", "Playback error", e)
        }
    }

    /**
     * Stops the MediaPlayer.
     */
    private fun stopStream() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    /**
     * Logs a message via Logcat and broadcasts it so the UI can display it.
     */
    private fun log(message: String) {
        Log.d("RadioService", message)
        val intent = Intent(RadioConstants.LOG_BROADCAST).apply {
            putExtra("log_message", message)
        }
        sendBroadcast(intent)
    }

    /**
     * Pings a given host and returns true if the host is reachable.
     */
    private fun ping(host: String): Boolean {
        return try {
            val address = InetAddress.getByName(host)
            address.isReachable(1000)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Measures latency (in ms) by pinging the host.
     */
    private fun measureLatency(host: String): Long {
        return try {
            val start = System.nanoTime()
            InetAddress.getByName(host).isReachable(1000)
            val end = System.nanoTime()
            (end - start) / 1_000_000
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Resolves DNS information for the host using the 'dig' command.
     */
    private fun resolveDns(host: String): String {
        return try {
            val process = ProcessBuilder("dig", host).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readText()
            result.lines().filter { it.contains("ANSWER") || it.contains("IN") }.joinToString("\n")
        } catch (e: Exception) {
            "DNS lookup failed"
        }
    }

    /**
     * Extracts the host from the provided URL.
     */
    private fun getHostFromUrl(url: String): String {
        return try {
            val uri = url.toUri()
            uri.host ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Ensures the MediaPlayer is stopped and released when the service is destroyed.
     */
    override fun onDestroy() {
        stopStream()
        super.onDestroy()
    }
}

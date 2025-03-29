package com.example.radiowaveproject.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.radiowaveproject.RadioConstants.ACTION_START
import com.example.radiowaveproject.RadioConstants.ACTION_STOP
import com.example.radiowaveproject.RadioConstants.EXTRA_STATION_NAME
import com.example.radiowaveproject.RadioConstants.radioStations

@Composable
fun RadioWaveScreen(logs: MutableList<String>) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf(radioStations.keys.first()) }
    var expanded by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Station selection dropdown (disabled when playing)
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { if (!isPlaying) expanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isPlaying
            ) {
                Text("🎧 Select Station: $selectedStation")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                radioStations.forEach { (name, _) ->
                    DropdownMenuItem(
                        onClick = {
                            selectedStation = name
                            logs.add("📻 Station selected: $name")
                            expanded = false
                        },
                        text = { Text(name) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Play/Stop button with click lock
        Button(
            onClick = {
                if (isClickable) {
                    isClickable = false
                    coroutineScope.launch {
                        delay(500)
                        isClickable = true
                    }
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        logs.add("▶ Starting: $selectedStation")
                        ContextCompat.startForegroundService(
                            context,
                            Intent(context, com.example.radiowaveproject.RadioService::class.java).apply {
                                action = ACTION_START
                                putExtra(EXTRA_STATION_NAME, selectedStation)
                            }
                        )
                    } else {
                        logs.add("⏹ Stopping: $selectedStation")
                        context.startService(
                            Intent(context, com.example.radiowaveproject.RadioService::class.java).apply {
                                action = ACTION_STOP
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isClickable
        ) {
            Text(if (isPlaying) "⏹ Stop" else "▶ Play")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spinner animation when playing
        if (isPlaying) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display logs
        Text(text = logs.joinToString("\n"))
    }
}

package com.example.radiowaveproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.radiowaveproject.RadioConstants.ACTION_START
import com.example.radiowaveproject.RadioConstants.ACTION_STOP
import com.example.radiowaveproject.RadioConstants.EXTRA_STATION_NAME
import com.example.radiowaveproject.RadioConstants.LOG_BROADCAST
import com.example.radiowaveproject.RadioConstants.radioStations
import com.example.radiowaveproject.ui.theme.RadioWaveProjectTheme
import com.example.radiowaveproject.NetworkPerformancePanel
import com.example.radiowaveproject.LogWindow
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Shared log list for broadcast messages from the services.
            val logs = remember { mutableStateListOf("📋 Ready") }
            LogReceiver(logs)

            RadioWaveProjectTheme {
                val navController = rememberNavController()
                MainScaffold(navController, logs)
            }
        }
    }
}

/**
 * MainScaffold contains a bottom navigation bar that lets users switch
 * between the Radio Service and Network Performance screens.
 */
@Composable
fun MainScaffold(navController: NavHostController, logs: MutableList<String>) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "radio",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("radio") { RadioServiceScreen(logs) }
            composable("network") { NetworkPerformanceScreen() }
        }
    }
}

/**
 * BottomNavigationBar shows two items:
 * • Radio – navigates to the screen that controls the radio service.
 * • Network – navigates to the screen that displays network performance.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("radio", "network")
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        "radio" -> Icon(Icons.Filled.Radio, contentDescription = "Radio")
                        "network" -> Icon(Icons.Filled.Wifi, contentDescription = "Network")
                    }
                },
                label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * RadioServiceScreen wraps the RadioWaveScreen composable that contains:
 * • A station selector and dropdown.
 * • Buttons that invoke the RadioService (using startForegroundService).
 */
@Composable
fun RadioServiceScreen(logs: MutableList<String>) {
    RadioWaveScreen(logs)
}

/**
 * NetworkPerformanceScreen wraps the NetworkPerformancePanel (from NetworkPerformance.kt)
 * in a centered container so that its sine wave animation and other info are clearly visible.
 */
@Composable
fun NetworkPerformanceScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        NetworkPerformancePanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

/**
 * LogReceiver listens for broadcast log messages from services (e.g. RadioService)
 * and appends them to the shared log list.
 */
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

/**
 * RadioWaveScreen displays a UI for selecting a radio station and starting/stopping the RadioService.
 */
@Composable
fun RadioWaveScreen(logs: MutableList<String>) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf(radioStations.keys.first()) }
    var expanded by remember { mutableStateOf(false) }
    // Controls whether the play/stop button can be clicked.
    var isClickable by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Station selection dropdown; disabled when playing.
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

        // Play/Stop button that starts or stops the RadioService.
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
                            Intent(context, RadioService::class.java).apply {
                                action = ACTION_START
                                putExtra(EXTRA_STATION_NAME, selectedStation)
                            }
                        )
                    } else {
                        logs.add("⏹ Stopping: $selectedStation")
                        context.startService(
                            Intent(context, RadioService::class.java).apply {
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

        // Spinner animation displayed while playing.
        if (isPlaying) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display logs for feedback.
        Text(text = logs.joinToString("\n"))
    }
}

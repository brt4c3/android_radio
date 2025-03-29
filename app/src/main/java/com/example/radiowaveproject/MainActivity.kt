package com.example.radiowaveproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.radiowaveproject.ui.LogReceiver
import com.example.radiowaveproject.ui.MainScaffold
import com.example.radiowaveproject.ui.theme.RadioWaveProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Shared log list for broadcast messages from services
            val logs = remember { mutableStateListOf("📋 Ready") }
            // Start listening for log broadcasts.
            LogReceiver(logs)
            RadioWaveProjectTheme {
                val navController = rememberNavController()
                MainScaffold(navController = navController, logs = logs)
            }
        }
    }
}

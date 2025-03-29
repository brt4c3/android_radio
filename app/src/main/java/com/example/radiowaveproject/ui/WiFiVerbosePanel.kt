package com.example.radiowaveproject.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun WiFiVerbosePanel(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Obtain WifiManager from application context.
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    // Create a state to hold the verbose WiFi connection information.
    val verboseInfo = remember { mutableStateOf("No connection info") }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Retrieve connection info from WifiManager.
                val wifiInfo = wifiManager?.connectionInfo
                // Use the verbose string representation.
                verboseInfo.value = wifiInfo?.toString() ?: "No connection info"
            } catch (e: Exception) {
                verboseInfo.value = "Error: ${e.message}"
            }
            delay(2000L)
        }
    }

    // Display the verbose info in a scrollable column.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "WiFi Verbose Info:",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Text(
            text = verboseInfo.value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

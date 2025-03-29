package com.example.radiowaveproject.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.radiowaveproject.BleScanner
import com.example.radiowaveproject.BleScanResult
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun BleScannerScreen() {
    val context = LocalContext.current

    // Define the required permission.
    val permission = Manifest.permission.BLUETOOTH_SCAN

    // Track whether permission is granted.
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher to request the permission.
    val permissionLauncher = rememberLauncherForActivityResult(contract = RequestPermission()) { isGranted ->
        hasPermission.value = isGranted
    }

    // If permission isn't granted, request it and show a message.
    if (!hasPermission.value) {
        LaunchedEffect(Unit) {
            permissionLauncher.launch(permission)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bluetooth scan permission is required.", color = Color.Black)
        }
        return
    }

    // Once permission is granted, create the BleScanner instance.
    val bleScanner = remember { BleScanner(context) }
    // Observe scan results (a list of BleScanResult).
    val resultsState = remember { derivedStateOf { bleScanner.results } }
    val results: List<BleScanResult> = resultsState.value

    // Manage scanning state.
    val isScanning = remember { mutableStateOf(false) }
    // Log messages specific to BLE scanning.
    val logs = remember { mutableStateListOf<String>() }
    // LazyListState for auto-scrolling the log window.
    val logListState = rememberLazyListState()
    LaunchedEffect(logs.size) {
        logListState.animateScrollToItem(logs.size)
    }

    // Optional: an infinite spinner rotation animation.
    val infiniteTransition = rememberInfiniteTransition()
    // Instead of using 'by', extract the value explicitly.
    val spinnerRotationState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        )
    )
    val spinnerRotation = spinnerRotationState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Single toggle button: start scan if not scanning; stop scan if scanning.
        Button(
            onClick = {
                if (!isScanning.value) {
                    bleScanner.startScan()
                    logs.add("Scan started.")
                    isScanning.value = true
                } else {
                    bleScanner.stopScan()
                    logs.add("Scan stopped.")
                    isScanning.value = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isScanning.value) "Stop Scan" else "Start Scan", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Display a spinner when scanning.
        if (isScanning.value) {
            // Here you can use spinnerRotation if you want to apply a rotation modifier.
            // For demonstration, we simply show a CircularProgressIndicator.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        // Display discovered BLE devices.
        if (results.isEmpty()) {
            Text(
                text = "No devices found",
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(8.dp)
            ) {
                items(results) { result ->
                    Text(text = "Name: ${result.device.name ?: "N/A"}", color = Color.White)
                    Text(text = "Address: ${result.device.address}", color = Color.White)
                    Text(text = "RSSI: ${result.rssi}", color = Color.White)
                    val scanRecordHex = result.scanRecord?.joinToString(" ") { byte ->
                        "%02X".format(byte)
                    } ?: "N/A"
                    Text(text = "ScanRecord: $scanRecordHex", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Log window: displays scan logs and auto-scrolls.
        LogWindow(logs = logs, listState = logListState)
    }
}

@Composable
fun LogWindow(logs: List<String>, listState: androidx.compose.foundation.lazy.LazyListState) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        items(logs) { log ->
            Text(text = log, color = Color.Black)
        }
    }
}

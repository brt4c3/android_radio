package com.example.radiowaveproject.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.radiowaveproject.BleScanner
import com.example.radiowaveproject.BleScanResult

@Composable
fun BleScannerScreen() {
    val context = LocalContext.current
    // Define the required permission.
    val permission = Manifest.permission.BLUETOOTH_SCAN
    // Remember whether permission is granted.
    val hasPermission = remember { mutableStateOf(
        androidx.core.app.ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    ) }
    // Create a permission launcher.
    val permissionLauncher = rememberLauncherForActivityResult(contract = RequestPermission()) { isGranted ->
        hasPermission.value = isGranted
    }

    // If permission is not granted, launch the request and show a message.
    if (!hasPermission.value) {
        LaunchedEffect(Unit) {
            permissionLauncher.launch(permission)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text("Bluetooth scan permission is required.")
        }
        return
    }

    // Now that permission is granted, create an instance of BleScanner.
    val bleScanner = remember { BleScanner(context) }
    // Observe scan results (a list of BleScanResult).
    val resultsState = remember { derivedStateOf { bleScanner.results } }
    val results: List<BleScanResult> = resultsState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Row with Start and Stop buttons.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { bleScanner.startScan() }) {
                Text("Start Scan", color = Color.White)
            }
            Button(onClick = { bleScanner.stopScan() }) {
                Text("Stop Scan", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Display discovered BLE devices.
        if (results.isEmpty()) {
            Text(
                text = "No devices found",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                items(items = results) { result ->
                    Text(text = "Name: ${result.device.name ?: "N/A"}", color = Color.Black)
                    Text(text = "Address: ${result.device.address}", color = Color.Black)
                    Text(text = "RSSI: ${result.rssi}", color = Color.Black)
                    val scanRecordHex = result.scanRecord?.joinToString(" ") { byte ->
                        "%02X".format(byte)
                    } ?: "N/A"
                    Text(text = "ScanRecord: $scanRecordHex", color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

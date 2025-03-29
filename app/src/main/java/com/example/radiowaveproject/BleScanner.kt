package com.example.radiowaveproject

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat

data class BleScanResult(
    val device: BluetoothDevice,
    val rssi: Int,
    val scanRecord: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BleScanResult

        if (rssi != other.rssi) return false
        if (device != other.device) return false
        if (!scanRecord.contentEquals(other.scanRecord)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rssi
        result = 31 * result + device.hashCode()
        result = 31 * result + (scanRecord?.contentHashCode() ?: 0)
        return result
    }
}

class BleScanner(context: Context) {
    // Use application context to avoid memory leaks.
    private val appContext = context.applicationContext
    private val bluetoothManager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    // Compose observable list for scan results.
    private val _results = mutableStateListOf<BleScanResult>()
    val results: List<BleScanResult>
        get() = _results

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let { scanResult ->
                // Add only if not already present.
                if (_results.none { it.device.address == scanResult.device.address }) {
                    _results.add(
                        BleScanResult(
                            device = scanResult.device,
                            rssi = scanResult.rssi,
                            scanRecord = scanResult.scanRecord?.bytes
                        )
                    )
                }
            }
        }
    }

    /**
     * Starts BLE scanning if BLUETOOTH_SCAN permission is granted.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        // Check permission at runtime.
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e("BleScanner", "BLUETOOTH_SCAN permission not granted. Cannot start scan.")
            return
        }
        _results.clear() // Clear previous results.
        scanner?.startScan(scanCallback)
    }

    /**
     * Stops BLE scanning if BLUETOOTH_SCAN permission is granted.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e("BleScanner", "BLUETOOTH_SCAN permission not granted. Cannot stop scan.")
            return
        }
        scanner?.stopScan(scanCallback)
    }
}

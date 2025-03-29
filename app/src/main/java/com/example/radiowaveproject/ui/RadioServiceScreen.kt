package com.example.radiowaveproject.ui

import androidx.compose.runtime.Composable

@Composable
fun RadioServiceScreen(logs: MutableList<String>) {
    // Delegate to the detailed radio UI
    RadioWaveScreen(logs)
}

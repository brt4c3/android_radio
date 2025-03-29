package com.example.radiowaveproject.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.radiowaveproject.NetworkPerformancePanel

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

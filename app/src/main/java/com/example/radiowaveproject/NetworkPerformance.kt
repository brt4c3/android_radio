package com.example.radiowaveproject

import android.net.TrafficStats
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.net.InetAddress
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun NetworkPerformancePanel(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var rxBytes by remember { mutableStateOf(0L) }
    var networkSpeed by remember { mutableStateOf(0L) }
    var dnsInfo by remember { mutableStateOf("Fetching DNS info...") }

    // Animation state
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wavePhase"
    )

    LaunchedEffect(Unit) {
        var lastRxBytes = TrafficStats.getTotalRxBytes()
        while (true) {
            delay(1000)
            val currentRxBytes = TrafficStats.getTotalRxBytes()
            val delta = currentRxBytes - lastRxBytes
            rxBytes = currentRxBytes
            networkSpeed = delta
            lastRxBytes = currentRxBytes

            // Try DNS lookup
            try {
                val address = InetAddress.getByName("google.com")
                dnsInfo = "DNS: google.com => ${address.hostAddress}"
            } catch (e: Exception) {
                dnsInfo = "DNS lookup failed"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Network Performance", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Sine Wave
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)) {
            val width = size.width
            val height = size.height
            val amplitude = height / 4
            val path = Path()
            path.moveTo(0f, height / 2)

            for (x in 0..width.toInt()) {
                val y = (amplitude * sin((x.toFloat() / width) * 4f * PI + wavePhase) + height / 2).toFloat()
                path.lineTo(x.toFloat(), y)
            }

            drawPath(path, color = Color.Cyan, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Received: ${rxBytes / 1024} KB", fontSize = 14.sp, color = Color.White)
        Text("Speed: ${networkSpeed / 1024} KB/s", fontSize = 14.sp, color = Color.White)
        Text(dnsInfo, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
    }
}
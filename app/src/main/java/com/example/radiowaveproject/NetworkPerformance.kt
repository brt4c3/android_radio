package com.example.radiowaveproject

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.TrafficStats
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign  // Use Compose TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun NetworkPerformancePanel(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var rxBytes = remember { mutableStateOf(0L) }
    var networkSpeed = remember { mutableStateOf(0L) }
    var dnsInfo = remember { mutableStateOf("Fetching DNS info...") }
    var ipInfo = remember { mutableStateOf("Fetching IP info...") }

    val infiniteTransition = rememberInfiniteTransition()
    val wavePhaseState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val wavePhase = wavePhaseState.value


    LaunchedEffect(Unit) {
        var lastRx = TrafficStats.getTotalRxBytes()
        while (true) {
            delay(1000)
            val currentRx = TrafficStats.getTotalRxBytes()
            networkSpeed.value = currentRx - lastRx
            rxBytes.value = currentRx
            lastRx = currentRx

            try {
                val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetwork
                val linkProps: LinkProperties? = connectivityManager.getLinkProperties(activeNetwork)
                // Retrieve DNS servers.
                val dnsList = linkProps?.dnsServers?.joinToString(", ") { it.hostAddress ?: "unknown" }
                dnsInfo.value = if (dnsList.isNullOrEmpty()) "No DNS servers found" else "DNS Servers: $dnsList"
                // Retrieve device IP addresses and default gateway using isDefaultRoute().
                val ipAddresses = linkProps?.linkAddresses?.joinToString(", ") { it.address.hostAddress }
                val gateway = linkProps?.routes?.find { it.isDefaultRoute() }?.gateway?.hostAddress
                val mtu = linkProps?.mtu ?: -1
                ipInfo.value = "Source IP: ${ipAddresses ?: "N/A"}\n" +
                        "Destination (Gateway): ${gateway ?: "N/A"}\n" +
                        "MTU: ${if (mtu > 0) mtu.toString() else "N/A"}"
            } catch (e: Exception) {
                dnsInfo.value = "DNS lookup failed: ${e.message}"
                ipInfo.value = "IP info not available"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Network Performance", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)) {
            val width = size.width
            val height = size.height
            val amplitude = height / 4
            val path = Path().apply { moveTo(0f, height / 2) }
            for (x in 0..width.toInt()) {
                val y = (amplitude * sin((x.toFloat() / width) * 4f * PI + wavePhase) + height / 2).toFloat()
                path.lineTo(x.toFloat(), y)
            }
            drawPath(path, color = Color.Cyan, style = Stroke(width = 4f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Received: ${rxBytes.value / 1024} KB", fontSize = 14.sp, color = Color.White)
        Text("Speed: ${networkSpeed.value / 1024} KB/s", fontSize = 14.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(dnsInfo.value, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(ipInfo.value, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
    }
}

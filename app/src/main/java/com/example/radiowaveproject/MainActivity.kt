package com.example.radiowaveproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.radiowaveproject.ui.theme.RadioWaveProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RadioWaveProjectTheme {
                RadioWaveScreen()
            }
        }
    }
}

@Composable
fun RadioWaveScreen() {
    var isPlaying by remember { mutableStateOf(false) }
    var isFM by remember { mutableStateOf(true) }
    var frequency by remember { mutableStateOf(89.1f) }
    var debugText by remember { mutableStateOf("Ready") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Debug Output
        Text(
            text = debugText,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Frequency Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { frequency -= 0.1f }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = "%.1f".format(frequency),
                onValueChange = { /* Read-only */ },
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { frequency += 0.1f }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AM/FM Toggle
        Button(
            onClick = {
                isFM = !isFM
                debugText = "Mode: ${if (isFM) "FM" else "AM"}"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isFM) "FM" else "AM")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Play/Stop Button
        Button(
            onClick = {
                isPlaying = !isPlaying
                if (isPlaying) {
                    startRadioService(context, frequency, isFM)
                } else {
                    stopRadioService(context)
                }
                debugText = "Playing: $isPlaying\nMode: ${if (isFM) "FM" else "AM"}\nFrequency: $frequency MHz"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isPlaying) "Stop" else "Play")
        }
    }
}

// Start Radio Service
fun startRadioService(context: Context, frequency: Float, isFM: Boolean) {
    val serviceIntent = Intent(context, RadioService::class.java).apply {
        action = RadioService.ACTION_START
        putExtra(RadioService.EXTRA_FREQUENCY, frequency)
        putExtra(RadioService.EXTRA_IS_FM, isFM)
    }
    context.startService(serviceIntent)
}

// Stop Radio Service
fun stopRadioService(context: Context) {
    val serviceIntent = Intent(context, RadioService::class.java).apply {
        action = RadioService.ACTION_STOP
    }
    context.startService(serviceIntent)
}

@Preview(showBackground = true)
@Composable
fun PreviewRadioWaveScreen() {
    RadioWaveProjectTheme {
        RadioWaveScreen()
    }
}

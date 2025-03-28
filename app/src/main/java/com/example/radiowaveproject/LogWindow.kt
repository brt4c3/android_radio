package com.example.radiowaveproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogWindow(logs: List<String>) {
    // LazyListState to control scrolling
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)  // adjust height as needed
            .background(Color.DarkGray)
            .padding(12.dp)
    ) {
        items(logs) { log ->
            Text(text = log, fontSize = 12.sp, color = Color.White)
        }
    }

    // Automatically scroll to the last log when logs change
    LaunchedEffect(logs.size) {
        listState.animateScrollToItem(index = logs.size)
    }
}

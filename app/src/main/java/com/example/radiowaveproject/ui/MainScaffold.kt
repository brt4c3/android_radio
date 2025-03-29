package com.example.radiowaveproject.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.radiowaveproject.RadioConstants
import com.example.radiowaveproject.RadioConstants.LOG_BROADCAST
import com.example.radiowaveproject.ui.theme.RadioWaveProjectTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Bluetooth

@Composable
fun MainScaffold(navController: NavHostController, logs: MutableList<String>) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "radio",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("radio") { RadioServiceScreen(logs) }
            composable("network") { NetworkPerformanceScreen() }
            composable("bleScanner") { BleScannerScreen() }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("radio", "network", "bleScanner")
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        "radio" -> Icon(Icons.Filled.Radio, contentDescription = "Radio")
                        "network" -> Icon(Icons.Filled.Wifi, contentDescription = "Network")
                        "bleScanner" -> Icon(Icons.Filled.Bluetooth, contentDescription = "BLE Scanner")
                    }
                },
                label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

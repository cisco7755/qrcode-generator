package com.qrforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qrforge.app.data.AppScreen
import com.qrforge.app.ui.QrViewModel
import com.qrforge.app.ui.ScannerViewModel
import com.qrforge.app.ui.screens.GeneratorScreen
import com.qrforge.app.ui.screens.ScannerScreen
import com.qrforge.app.ui.theme.QRForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRForgeTheme {
                var currentScreen by rememberSaveable { mutableStateOf(AppScreen.GENERATE) }
                val qrViewModel: QrViewModel = viewModel()
                val scannerViewModel: ScannerViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.GENERATE,
                                onClick = { currentScreen = AppScreen.GENERATE },
                                icon = { Icon(Icons.Default.QrCode2, contentDescription = null) },
                                label = { Text("Generate") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.SCAN,
                                onClick = { currentScreen = AppScreen.SCAN },
                                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                                label = { Text("Scan") }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentScreen) {
                        AppScreen.GENERATE -> GeneratorScreen(
                            viewModel = qrViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppScreen.SCAN -> ScannerScreen(
                            viewModel = scannerViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

package com.qrforge.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.qrforge.app.data.QrInputType
import com.qrforge.app.ui.QrViewModel
import com.qrforge.app.ui.components.InputTabs
import com.qrforge.app.ui.components.PhoneInput
import com.qrforge.app.ui.components.QrPreview
import com.qrforge.app.ui.components.TextUrlInput
import com.qrforge.app.ui.components.WifiInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(viewModel: QrViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("QR Forge") },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            InputTabs(
                selected = state.inputType,
                onSelect = viewModel::onInputTypeChanged
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (state.inputType) {
                    QrInputType.TEXT -> TextUrlInput(
                        value = state.textInput,
                        onValueChange = viewModel::onTextChanged,
                        onGenerate = viewModel::generateQr
                    )
                    QrInputType.PHONE -> PhoneInput(
                        value = state.phoneInput,
                        onValueChange = viewModel::onPhoneChanged,
                        onGenerate = viewModel::generateQr
                    )
                    QrInputType.WIFI -> WifiInput(
                        ssid = state.wifiSsid,
                        password = state.wifiPassword,
                        encryption = state.wifiEncryption,
                        hidden = state.wifiHidden,
                        onSsidChange = viewModel::onWifiSsidChanged,
                        onPasswordChange = viewModel::onWifiPasswordChanged,
                        onEncryptionChange = viewModel::onWifiEncryptionChanged,
                        onHiddenChange = viewModel::onWifiHiddenChanged,
                        onGenerate = viewModel::generateQr
                    )
                }

                Button(
                    onClick = viewModel::generateQr,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.QrCode2, contentDescription = null)
                    Spacer(Modifier.padding(start = 8.dp))
                    Text("Generate QR Code")
                }

                AnimatedVisibility(
                    visible = state.qrBitmap != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut()
                ) {
                    state.qrBitmap?.let { bitmap ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Spacer(Modifier.height(4.dp))

                            QrPreview(bitmap = bitmap)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = viewModel::saveQr,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(Modifier.padding(start = 6.dp))
                                    Text("Save")
                                }
                                FilledTonalButton(
                                    onClick = viewModel::shareQr,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                    Spacer(Modifier.padding(start = 6.dp))
                                    Text("Share")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

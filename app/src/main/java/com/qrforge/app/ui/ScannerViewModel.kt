package com.qrforge.app.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScanUiState(
    val scannedContent: String? = null,
    val isScanning: Boolean = true,
    val snackbarMessage: String? = null,
)

class ScannerViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(ScanUiState())
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    fun onQrDetected(content: String) {
        if (_state.value.scannedContent == content) return
        _state.value = _state.value.copy(
            scannedContent = content,
            isScanning = false
        )
    }

    fun copyToClipboard() {
        val text = _state.value.scannedContent ?: return
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("QR Code", text))
        _state.value = _state.value.copy(snackbarMessage = "Copied to clipboard")
    }

    fun shareContent() {
        val text = _state.value.scannedContent ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share scanned content").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(chooser)
    }

    fun openInBrowser() {
        val text = _state.value.scannedContent ?: return
        if (!text.startsWith("http://") && !text.startsWith("https://")) return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            app.startActivity(intent)
        } catch (_: Exception) {
            _state.value = _state.value.copy(snackbarMessage = "No app found to open this URL")
        }
    }

    fun resumeScanning() {
        _state.value = ScanUiState()
    }

    fun clearSnackbar() {
        _state.value = _state.value.copy(snackbarMessage = null)
    }
}

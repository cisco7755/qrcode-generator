package com.qrforge.app.ui

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.qrforge.app.data.QrInputType
import com.qrforge.app.data.WifiEncryption
import com.qrforge.app.util.ImageUtils
import com.qrforge.app.util.QrGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class QrUiState(
    val inputType: QrInputType = QrInputType.TEXT,
    val textInput: String = "",
    val phoneInput: String = "",
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiEncryption: WifiEncryption = WifiEncryption.WPA,
    val wifiHidden: Boolean = false,
    val qrBitmap: Bitmap? = null,
    val snackbarMessage: String? = null,
)

class QrViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(QrUiState())
    val state: StateFlow<QrUiState> = _state.asStateFlow()

    fun onInputTypeChanged(type: QrInputType) {
        _state.value = _state.value.copy(inputType = type, qrBitmap = null)
    }

    fun onTextChanged(text: String) {
        _state.value = _state.value.copy(textInput = text)
    }

    fun onPhoneChanged(phone: String) {
        _state.value = _state.value.copy(phoneInput = phone)
    }

    fun onWifiSsidChanged(ssid: String) {
        _state.value = _state.value.copy(wifiSsid = ssid)
    }

    fun onWifiPasswordChanged(password: String) {
        _state.value = _state.value.copy(wifiPassword = password)
    }

    fun onWifiEncryptionChanged(encryption: WifiEncryption) {
        _state.value = _state.value.copy(wifiEncryption = encryption)
    }

    fun onWifiHiddenChanged(hidden: Boolean) {
        _state.value = _state.value.copy(wifiHidden = hidden)
    }

    fun clearSnackbar() {
        _state.value = _state.value.copy(snackbarMessage = null)
    }

    fun generateQr() {
        val s = _state.value
        val content = when (s.inputType) {
            QrInputType.TEXT -> s.textInput.trim()
            QrInputType.PHONE -> QrGenerator.formatPhone(s.phoneInput.trim())
            QrInputType.WIFI -> QrGenerator.formatWifi(
                ssid = s.wifiSsid.trim(),
                password = s.wifiPassword.trim(),
                encryption = s.wifiEncryption.protocol,
                hidden = s.wifiHidden
            )
        }

        if (content.isBlank() || (s.inputType == QrInputType.WIFI && s.wifiSsid.isBlank())) {
            _state.value = s.copy(snackbarMessage = "Please enter content to generate a QR code")
            return
        }

        val bitmap = QrGenerator.generate(content, size = 1024)
        _state.value = s.copy(qrBitmap = bitmap)
    }

    fun saveQr() {
        val bitmap = _state.value.qrBitmap ?: return
        val uri = ImageUtils.saveBitmap(app, bitmap)
        val message = if (uri != null) "QR code saved to Pictures/QRForge" else "Failed to save"
        _state.value = _state.value.copy(snackbarMessage = message)
    }

    fun shareQr() {
        val bitmap = _state.value.qrBitmap ?: return
        val uri = ImageUtils.getShareableUri(app, bitmap) ?: return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Share QR Code").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(chooser)
    }
}

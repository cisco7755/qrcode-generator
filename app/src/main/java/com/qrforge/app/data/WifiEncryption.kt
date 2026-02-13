package com.qrforge.app.data

enum class WifiEncryption(val label: String, val protocol: String) {
    WPA("WPA/WPA2/WPA3", "WPA"),
    WEP("WEP", "WEP"),
    NONE("None", "nopass")
}

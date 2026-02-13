package com.qrforge.app.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

object QrGenerator {

    fun generate(content: String, size: Int = 512): Bitmap? {
        if (content.isBlank()) return null

        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.MARGIN to 1
            )
            val bitMatrix = QRCodeWriter().encode(
                content, BarcodeFormat.QR_CODE, size, size, hints
            )
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    fun formatPhone(number: String): String = "tel:$number"

    fun formatWifi(ssid: String, password: String, encryption: String, hidden: Boolean): String {
        val escapedSsid = escapeWifiField(ssid)
        val escapedPassword = escapeWifiField(password)
        val hiddenFlag = if (hidden) "H:true;" else ""
        return "WIFI:T:$encryption;S:$escapedSsid;P:$escapedPassword;$hiddenFlag;"
    }

    private fun escapeWifiField(value: String): String {
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace(":", "\\:")
    }
}

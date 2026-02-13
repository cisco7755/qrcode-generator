package com.qrforge.app.util

import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

object QrDecoder {

    private val reader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                DecodeHintType.TRY_HARDER to true
            )
        )
    }

    fun decode(imageProxy: ImageProxy): String? {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val source = PlanarYUVLuminanceSource(
            bytes,
            imageProxy.width, imageProxy.height,
            0, 0,
            imageProxy.width, imageProxy.height,
            false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        return try {
            reader.decodeWithState(bitmap).text
        } catch (_: Exception) {
            null
        } finally {
            reader.reset()
        }
    }
}

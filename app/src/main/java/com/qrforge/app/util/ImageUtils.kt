package com.qrforge.app.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

object ImageUtils {

    fun saveBitmap(context: Context, bitmap: Bitmap, filename: String = "QRForge_${System.currentTimeMillis()}"): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveWithMediaStore(context, bitmap, filename)
        } else {
            saveToLegacyStorage(context, bitmap, filename)
        }
    }

    private fun saveWithMediaStore(context: Context, bitmap: Bitmap, filename: String): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRForge")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null

        resolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)

        return uri
    }

    @Suppress("DEPRECATION")
    private fun saveToLegacyStorage(context: Context, bitmap: Bitmap, filename: String): Uri? {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "QRForge"
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "$filename.png")
        file.outputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        // Notify media scanner
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = Uri.fromFile(file)
        }
        context.sendBroadcast(mediaScanIntent)

        return Uri.fromFile(file)
    }

    fun getShareableUri(context: Context, bitmap: Bitmap): Uri? {
        val cacheDir = File(context.cacheDir, "shared_qr")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val file = File(cacheDir, "qr_${System.currentTimeMillis()}.png")
        file.outputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}

package com.example.chatease.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.graphics.scale
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.max

class ImageUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun compressImage(uri: Uri): Uri = compressImage(context, uri)
    fun isFileSizeValid(uri: Uri): Boolean = isFileSizeValid(context, uri)
}

fun compressImage(context: Context, uri: Uri): Uri {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream")

    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream.close()

    val maxSize = 1024
    val width = originalBitmap.width
    val height = originalBitmap.height

    val scale = max(width, height).toFloat() / maxSize
    val resizedBitmap: Bitmap = if (scale > 1) {
        val newWidth = (width / scale).toInt()
        val newHeight = (height / scale).toInt()
        originalBitmap.scale(newWidth, newHeight)
    } else {
        originalBitmap
    }

    val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")

    FileOutputStream(file).use { outputStream ->
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    }

    return Uri.fromFile(file)
}

fun isFileSizeValid(context: Context, imageUri: Uri): Boolean {
    val maxSize = 5 * 1024 * 1024 // 5MB

    if (imageUri.scheme == "file") {
        val fileSize = File(imageUri.path!!).length()
        return fileSize <= maxSize
    }

    val cursor = context.contentResolver.query(imageUri, null, null, null, null)
    cursor?.use {
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex != -1) {
            it.moveToFirst()
            val fileSize = it.getLong(sizeIndex)
            return fileSize <= maxSize
        }
    }
    return false
}
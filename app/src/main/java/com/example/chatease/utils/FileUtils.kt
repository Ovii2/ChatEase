package com.example.chatease.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun String.toTruncatedFileName(): String {
    if (this.length >= 35) {
        return this.take(20) + "..." + this.takeLast(12)
    }
    return this
}

fun Long.toFormattedFileSize(): String {
    val oneKb = 1024L
    val oneMb = oneKb * 1024

    return when {
        this < oneKb -> "$this B"
        this < oneMb -> "%.2f KB".format(this.toDouble() / oneKb)
        else -> "%.2f MB".format(this.toDouble() / oneMb)
    }
}

fun openFile(
    context: Context,
    uri: Uri,
    mimeType: String
) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, null)
    )
}
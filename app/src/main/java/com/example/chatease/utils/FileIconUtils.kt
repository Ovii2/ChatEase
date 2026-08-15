package com.example.chatease.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.chatease.R

@Composable
fun String.toFileIcon(): Painter {
    val extension = substringAfterLast(".", "").lowercase()

    return when (extension) {
        "pdf" -> painterResource(R.drawable.ic_pdf)
        "docx" -> painterResource(R.drawable.ic_docx)
        "csv" -> painterResource(R.drawable.ic_csv)
        else -> if (isSystemInDarkTheme()) painterResource(R.drawable.ic_file_white) else painterResource(
            R.drawable.ic_file
        )
    }
}
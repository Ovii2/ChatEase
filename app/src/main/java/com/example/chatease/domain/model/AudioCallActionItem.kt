package com.example.chatease.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AudioCallActionItem(
    @StringRes val label: Int,
    val icon: ImageVector
)

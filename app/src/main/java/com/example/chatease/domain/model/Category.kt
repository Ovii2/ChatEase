package com.example.chatease.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String = "",
    @StringRes val name: Int,
    val icon: ImageVector
)

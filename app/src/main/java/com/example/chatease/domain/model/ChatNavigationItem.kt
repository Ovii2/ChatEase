package com.example.chatease.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class ChatNavigationItem(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector? = null,
    @DrawableRes val image: Int? = null,
    val badgeCount: Int = 0,
    val showBadge: Boolean = false
)

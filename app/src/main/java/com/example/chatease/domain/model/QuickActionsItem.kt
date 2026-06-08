package com.example.chatease.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class QuickActionsItem(
    @DrawableRes val image: Int? = null,
    val icon: ImageVector? = null,
    @StringRes val label: Int
)

package com.example.chatease.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ContactActionItem(
    val label: Int,
    val icon: ImageVector,
    val isDestructive: Boolean = false
)

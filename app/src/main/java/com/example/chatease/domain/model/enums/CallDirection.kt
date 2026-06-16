package com.example.chatease.domain.model.enums

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

enum class CallDirection {
    MISSED,
    INCOMING,
    OUTGOING
}

fun CallDirection.toScreenName(): Int = when (this) {
    CallDirection.MISSED -> R.string.missed
    CallDirection.INCOMING -> R.string.incoming
    CallDirection.OUTGOING -> R.string.outgoing
}

@Composable
fun CallDirection.color(): Color = when (this) {
    CallDirection.MISSED -> MaterialTheme.colorScheme.error
    CallDirection.INCOMING -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight
    CallDirection.OUTGOING -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
}
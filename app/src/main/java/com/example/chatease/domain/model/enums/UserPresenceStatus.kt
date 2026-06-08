package com.example.chatease.domain.model.enums

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.awayYellow
import com.example.chatease.presentation.ui.theme.awayYellowDark
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

enum class UserPresenceStatus {

    ONLINE,
    AWAY,
    OFFLINE
}

@Composable
fun UserPresenceStatus.toScreenName(): String {
    return when (this) {
        UserPresenceStatus.ONLINE -> stringResource(R.string.online)
        UserPresenceStatus.AWAY -> stringResource(R.string.away)
        UserPresenceStatus.OFFLINE -> stringResource(R.string.offline)
    }
}

@Composable
fun UserPresenceStatus.color(): Color {
    return when (this) {
        UserPresenceStatus.ONLINE -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight
        UserPresenceStatus.AWAY -> if (isSystemInDarkTheme()) awayYellowDark else awayYellow
        UserPresenceStatus.OFFLINE -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }
}
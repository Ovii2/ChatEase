package com.example.chatease.domain.model.enums

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

enum class CallStatus {
    CALLING,
    INCOMING,
    CONNECTED,
    ENDED,
    DECLINED,
    MISSED,
    CANCELED
}

@Composable
fun CallStatus.toScreenName(minutes: Int? = null, seconds: Int? = null) = when (this) {
    CallStatus.CALLING -> stringResource(R.string.calling)
    CallStatus.INCOMING -> stringResource(R.string.incoming_call)
    CallStatus.CONNECTED -> stringResource(
        R.string.call_duration_format,
        minutes ?: 0,
        seconds ?: 0
    )

    CallStatus.ENDED -> stringResource(R.string.call_ended)
    CallStatus.DECLINED -> stringResource(R.string.declined)
    CallStatus.MISSED -> stringResource(R.string.missed)
    CallStatus.CANCELED -> stringResource(R.string.canceled)
}

@Composable
fun CallStatus.color(modifier: Modifier = Modifier) = when (this) {
    CallStatus.CALLING,
    CallStatus.INCOMING,
    CallStatus.CONNECTED -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight

    else -> MaterialTheme.colorScheme.error

}
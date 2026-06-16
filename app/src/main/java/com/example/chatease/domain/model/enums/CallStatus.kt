package com.example.chatease.domain.model.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.chatease.R

enum class CallStatus {
    CALLING,
    INCOMING,
    CONNECTED,
    ENDED
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
}
package com.example.chatease.presentation.ui.screens.audio_call.layouts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallActionSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallBottomSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallTopSection

@Composable
fun AudioCallCompactLayout(
    modifier: Modifier = Modifier,
    callStatus: CallStatus,
    user: User,
    onAcceptCall: (String) -> Unit,
    onCancelCall: (String) -> Unit,
    callId: String,
    callDurationSeconds: Int
) {
    AudioCallTopSection(
        modifier = modifier,
        callStatus = callStatus,
        user = user,
        callDurationSeconds = callDurationSeconds
    )

    AudioCallActionSection(
        callStatus = callStatus
    )

    AudioCallBottomSection(
        callStatus = callStatus,
        onAcceptCall = { onAcceptCall(callId) },
        onCancelCall = { onCancelCall(callId) }
    )
}
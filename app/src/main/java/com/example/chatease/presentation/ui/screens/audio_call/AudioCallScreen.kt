package com.example.chatease.presentation.ui.screens.audio_call

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.screens.audio_call.layouts.AudioCallCompactLayout
import com.example.chatease.presentation.ui.screens.shared.calls.ActiveCallScreenLayout
import com.example.chatease.presentation.ui.viewmodel.CallViewModel

@Composable
fun AudioCallScreen(
    modifier: Modifier = Modifier,
    callViewModel: CallViewModel = hiltViewModel(),
    callId: String,
    onNavigateBack: () -> Unit
) {
    val call by callViewModel.call.collectAsState()
    val currentUserId = callViewModel.currentUserId
    val callStatus = when {
        call == null -> CallStatus.CALLING
        call?.status == CallStatus.CALLING && call?.receiverId == currentUserId -> CallStatus.INCOMING
        else -> call?.status ?: CallStatus.CALLING
    }
    val user by callViewModel.user.collectAsState()

    LaunchedEffect(callId) {
        callViewModel.observeCall(callId)
    }

    LaunchedEffect(callStatus) {
        when (callStatus) {
            CallStatus.CALLING -> {}

            CallStatus.INCOMING -> {}

            CallStatus.CONNECTED -> {}

            CallStatus.ENDED,
            CallStatus.DECLINED,
            CallStatus.CANCELED -> onNavigateBack()

            CallStatus.MISSED -> {}
        }

    }

    ActiveCallScreenLayout(
        callId = callId
    ) {
        AudioCallCompactLayout(
            callStatus = callStatus,
            user = user,
            onAcceptCall = {
                callViewModel.answerCall(callId)
            },
            onCancelCall = {
                callViewModel.cancelCall(callId)
            },
            callId = callId
        )
    }
}

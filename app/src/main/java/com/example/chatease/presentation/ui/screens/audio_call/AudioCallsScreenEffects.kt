package com.example.chatease.presentation.ui.screens.audio_call

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.navigation.Screens
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AudioCallsScreenEffects(
    modifier: Modifier = Modifier,
    callId: String,
    onObserveCall: (String) -> Unit,
    callStatus: CallStatus,
    onNavigateBack: () -> Unit,
    onCleanUpCall: () -> Unit,
    currentRoute: String,
    hasAudioPermission: Boolean,
    onIncrementCallDuration: () -> Unit,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    LaunchedEffect(callId) {
        onObserveCall(callId)
    }

    LaunchedEffect(callStatus) {
        when (callStatus) {
            CallStatus.CALLING -> {}

            CallStatus.INCOMING -> {}

            CallStatus.CONNECTED -> {}

            CallStatus.ENDED,
            CallStatus.DECLINED,
            CallStatus.CANCELED -> onNavigateBack()

            CallStatus.MISSED -> {
                delay(600.milliseconds)
                onCleanUpCall()
                if (currentRoute != Screens.Home.route) {
                    onNavigateBack()
                }
            }
        }
    }

    LaunchedEffect(callStatus) {
        if (callStatus == CallStatus.CONNECTED) {
            while (true) {
                delay(1000.milliseconds)
                onIncrementCallDuration()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

}
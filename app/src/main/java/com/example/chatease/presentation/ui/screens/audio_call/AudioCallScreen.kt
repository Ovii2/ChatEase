package com.example.chatease.presentation.ui.screens.audio_call

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
    onNavigateBack: () -> Unit,
    currentRoute: String
) {
    val call by callViewModel.call.collectAsState()
    val currentUserId = callViewModel.currentUserId
    val callStatus = when {
        call == null -> CallStatus.CALLING
        call?.status == CallStatus.CALLING && call?.receiverId == currentUserId -> CallStatus.INCOMING
        else -> call?.status ?: CallStatus.CALLING
    }
    val user by callViewModel.user.collectAsState()
    var callDurationSeconds by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current
    val hasAudioPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
    val isMuted by callViewModel.isMuted.collectAsState()
    val isSpeakerEnabled by callViewModel.isSpeakerEnabled.collectAsState()

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                callViewModel.cancelCall(callId)
                onNavigateBack()
            }
        }

    AudioCallsScreenEffects(
        callId = callId,
        onObserveCall = { callViewModel.observeCall(callId) },
        callStatus = callStatus,
        onNavigateBack = onNavigateBack,
        onCleanUpCall = callViewModel::cleanUpCall,
        currentRoute = currentRoute,
        hasAudioPermission = hasAudioPermission,
        onIncrementCallDuration = { callDurationSeconds++ },
        permissionLauncher = permissionLauncher
    )

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
            onEndCall = {
                callViewModel.endCall(callId)
            },
            onDeclineCall = {
                callViewModel.declineCall(callId)
            },
            callId = callId,
            callDurationSeconds = callDurationSeconds,
            onSpeakerToggle = { callViewModel.toggleSpeaker() },
            isSpeakerEnabled = isSpeakerEnabled,
            onMute = { callViewModel.toggleMute() },
            isMuted = isMuted,
        )
    }
}

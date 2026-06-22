package com.example.chatease.presentation.ui.screens.connected_call

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.screens.shared.calls.ActiveCallScreenLayout
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallActionSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallBottomSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallTopSection
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.CallViewModel

@Composable
fun ConnectedCallScreen(
    modifier: Modifier = Modifier,
    callViewModel: CallViewModel = hiltViewModel(),
    callId: String,
    onNavigateToChatScreen: () -> Unit
) {
    val user by callViewModel.user.collectAsState()
    val call by callViewModel.call.collectAsState()
    val callStatus = CallStatus.CONNECTED

    LaunchedEffect(call?.status) {
        when (call?.status) {
            CallStatus.ENDED -> onNavigateToChatScreen()
            else -> Unit
        }
    }

    LaunchedEffect(callId) {
        callViewModel.observeCall(callId)
    }

    ActiveCallScreenLayout(
        callId = callId
    ) {
        AudioCallTopSection(
            callStatus = callStatus,
            user = user
        )
        AudioCallActionSection(
            callStatus = callStatus
        )
        AudioCallBottomSection(
            callStatus = callStatus,
            onCancelCall = {
                callViewModel.endCall(callId)
            }
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ConnectedCallScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConnectedCallScreen(
                    callId = "",
                    onNavigateToChatScreen = {},
                )
            }
        }
    }
}

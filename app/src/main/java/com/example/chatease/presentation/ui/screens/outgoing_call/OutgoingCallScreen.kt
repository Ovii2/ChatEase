package com.example.chatease.presentation.ui.screens.outgoing_call

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
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.outgoing_call.layouts.OutgoingCallScreenCompactLayout
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.CallViewModel

@Composable
fun OutgoingCallScreen(
    modifier: Modifier = Modifier,
    callViewModel: CallViewModel = hiltViewModel(),
    onNavigateToConnectedCallScreen: () -> Unit,
    onNavigateBack: () -> Unit,
    callId: String
) {
    val call by callViewModel.call.collectAsState()
    val user by callViewModel.user.collectAsState()

    LaunchedEffect(call?.status) {
        when (call?.status) {
            CallStatus.CONNECTED -> {
                onNavigateToConnectedCallScreen()
            }

            CallStatus.DECLINED,
            CallStatus.CANCELED,
            CallStatus.ENDED,
            CallStatus.MISSED -> {
            }

            else -> Unit
        }
    }

    LaunchedEffect(callId) {
        callViewModel.observeCall(callId)
    }

    OutgoingCallScreenCompactLayout(
        callId = call?.id ?: "",
        user = user,
        onCancelCall = {
            call?.id?.let { callId ->
                callViewModel.cancelCall(callId)
            }
        },
        onNavigateBack = onNavigateBack
    )
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OutgoingCallScreenPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutgoingCallScreenCompactLayout(
                    callId = "",
                    user = user,
                    onCancelCall = {},
                    onNavigateBack = {}
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.outgoing_call.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.calls.ActiveCallScreenLayout
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallActionSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallBottomSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallTopSection
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun OutgoingCallScreenCompactLayout(
    modifier: Modifier = Modifier,
    callId: String,
    user: User,
    onCancelCall: () -> Unit
) {
    ActiveCallScreenLayout(
        callId = callId
    ) {
        AudioCallTopSection(
            callStatus = CallStatus.CALLING,
            user = user
        )
        AudioCallActionSection(
            callStatus = CallStatus.CALLING
        )
        AudioCallBottomSection(
            callStatus = CallStatus.CALLING,
            onCancelCall = {
                onCancelCall()
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OutgoingCallScreenCompactLayoutPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    ChatEaseTheme() {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutgoingCallScreenCompactLayout(
                    callId = "",
                    user = user,
                    onCancelCall = {}
                )
            }
        }
    }
}

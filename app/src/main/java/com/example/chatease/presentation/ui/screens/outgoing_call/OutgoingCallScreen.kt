package com.example.chatease.presentation.ui.screens.outgoing_call

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallActionSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallBottomSection
import com.example.chatease.presentation.ui.screens.shared.calls.AudioCallTopSection
import com.example.chatease.presentation.ui.screens.shared.calls.CommonCallsTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun OutgoingCallScreen(modifier: Modifier = Modifier) {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.primary
                )
            )
        )
    ) {
        Scaffold(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp),
            containerColor = Color.Transparent,
            topBar = {
                CommonCallsTopBar()
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
            ) {
                AudioCallTopSection(
                    callStatus = CallStatus.CALLING,
                    user = user
                )
                AudioCallActionSection(
                    callStatus = CallStatus.CALLING
                )
                AudioCallBottomSection(
                    callStatus = CallStatus.CALLING
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
         uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OutgoingCallScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutgoingCallScreen()
            }
        }
    }
}
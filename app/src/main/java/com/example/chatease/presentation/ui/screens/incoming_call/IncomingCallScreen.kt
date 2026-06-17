package com.example.chatease.presentation.ui.screens.incoming_call

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
fun IncomingCallScreen(modifier: Modifier = Modifier) {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    ActiveCallScreenLayout(
        backgroundColors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.secondary
        )
    ) {
        AudioCallTopSection(
            callStatus = CallStatus.INCOMING,
            user = user
        )
        AudioCallActionSection(
            callStatus = CallStatus.INCOMING
        )
        AudioCallBottomSection(
            callStatus = CallStatus.INCOMING
        )
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun IncomingCallScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IncomingCallScreen()
            }
        }
    }
}
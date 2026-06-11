package com.example.chatease.presentation.ui.screens.other_user_profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileAboutSection
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileActionsSection
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileTopSection
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun OtherUserProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    val isConnected = false

    Scaffold(topBar = {
        CommonTopBar(
            onBackClick = onBackClick,
        )
    }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OtherUserProfileTopSection(
                user = user,
                isConnected = isConnected,
                onSendRequest = {},
                onSendMessage = {},
            )
            OtherUserProfileActionsSection(
                onViewPhotoClick = {},
                onBlockClick = {},
                onReportClick = {}
            )
            OtherUserProfileAboutSection()
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OtherUserProfileScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileScreen(
                    onBackClick = {}
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.other_user_profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileAboutSection
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileActionsSection
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileTopSection
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.OtherUserProfileViewModel

@Composable
fun OtherUserProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    otherUserProfileViewModel: OtherUserProfileViewModel = hiltViewModel(),
    userId: String
) {
    val user by otherUserProfileViewModel.user.collectAsState()
    val isConnected by otherUserProfileViewModel.isUserConnected.collectAsState()

    LaunchedEffect(userId) {
        otherUserProfileViewModel.loadUser(userId)
        otherUserProfileViewModel.checkIfUserConnected(userId)
    }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = modifier
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState()),
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
                    onBackClick = {},
                    userId = "",
                )
            }
        }
    }
}

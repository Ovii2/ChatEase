package com.example.chatease.presentation.ui.screens.other_user_profile

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileAboutSection
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileActionsSection
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileTopSection
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.user.BlockUserBottomSheet
import com.example.chatease.presentation.ui.viewmodel.OtherUserProfileViewModel

@Composable
fun OtherUserProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    otherUserProfileViewModel: OtherUserProfileViewModel = hiltViewModel(),
    userId: String,
    onNavigateToChatScreen: (String) -> Unit
) {
    val user by otherUserProfileViewModel.user.collectAsState()
    val isConnected by otherUserProfileViewModel.isUserConnected.collectAsState()
    val isBlocked by otherUserProfileViewModel.isUserBlocked.collectAsState()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userId) {
        otherUserProfileViewModel.loadUser(userId)
        otherUserProfileViewModel.checkIfUserConnected(userId)
        otherUserProfileViewModel.checkIfUserIsBlocked(userId)
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
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileTopSection(
                    user = user,
                    isConnected = isConnected,
                    onSendRequest = {},
                    onSendMessage = {
                        otherUserProfileViewModel.createNewConversation(
                            selectedUserId = userId,
                            onConversationCreated = { onNavigateToChatScreen(it) }
                        )
                    },
                    isBlocked = isBlocked,
                    onUnblockClick = { otherUserProfileViewModel.unblockUser(userId) },
                )
                if (!isBlocked) {
                    OtherUserProfileActionsSection(
                        onViewPhotoClick = {},
                        onBlockClick = {
                            showBottomSheet = true
                        },
                        onReportClick = {}
                    )
                    OtherUserProfileAboutSection(modifier = Modifier.padding(bottom = 16.dp))
                }
            }
        }
    }
    if (showBottomSheet) {
        BlockUserBottomSheet(
            onDismiss = { showBottomSheet = false },
            onBlockClick = {
                otherUserProfileViewModel.blockUser(userId)
                showBottomSheet = false
            },
        )
    }
}

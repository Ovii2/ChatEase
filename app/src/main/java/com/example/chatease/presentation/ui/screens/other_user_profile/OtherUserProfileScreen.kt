package com.example.chatease.presentation.ui.screens.other_user_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfilePhotoDialog
import com.example.chatease.presentation.ui.screens.other_user_profile.components.OtherUserProfileScreenContent
import com.example.chatease.presentation.ui.screens.shared.bottom_sheet.CommonChatBottomSheet
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.OtherUserProfileViewModel

@Composable
fun OtherUserProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    otherUserProfileViewModel: OtherUserProfileViewModel = hiltViewModel(),
    userId: String,
    onNavigateToChatScreen: (String) -> Unit,
    onNavigateToMutualGroup: (String) -> Unit
) {
    val user by otherUserProfileViewModel.user.collectAsState()
    val isConnected by otherUserProfileViewModel.isUserConnected.collectAsState()
    val isBlocked by otherUserProfileViewModel.isUserBlocked.collectAsState()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val mutualGroups by otherUserProfileViewModel.mutualGroups.collectAsState()
    var isPhotoVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userId) {
        otherUserProfileViewModel.loadUser(userId)
        otherUserProfileViewModel.checkIfUserConnected(userId)
        otherUserProfileViewModel.checkIfUserIsBlocked(userId)
        otherUserProfileViewModel.getMutualGroups(userId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.tertiary,
                        )
                    )
                )
        )
        Scaffold(
            modifier = Modifier
                .padding(vertical = 8.dp),
            contentWindowInsets = WindowInsets(0),
            containerColor = Color.Transparent,
            topBar = {
                CommonTopBar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onBackClick = onBackClick,
                    isTransparent = true
                )
            }) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                contentAlignment = Alignment.Center
            ) {
                OtherUserProfileScreenContent(
                    paddingValues = paddingValues,
                    user = user,
                    isConnected = isConnected,
                    onSendMessage = {
                        otherUserProfileViewModel.createNewConversation(
                            selectedUserId = userId,
                            onConversationCreated = { onNavigateToChatScreen(it) }
                        )
                    },
                    isBlocked = isBlocked,
                    onUnblockClick = { otherUserProfileViewModel.unblockUser(userId) },
                    onBlockClick = { showBottomSheet = true },
                    groups = mutualGroups,
                    onNavigateToMutualGroup = onNavigateToMutualGroup,
                    mutualGroups = mutualGroups,
                    onViewPhotoClick = {
                        isPhotoVisible = true
                    },
                    hasProfilePhoto = user.imageUrl != null,
                )
            }
        }
    }

    if (showBottomSheet) {
        CommonChatBottomSheet(
            onDismiss = { showBottomSheet = false },
            onClick = {
                otherUserProfileViewModel.blockUser(userId)
                showBottomSheet = false
            },
            title = R.string.block_user_title,
            text = R.string.block_user_message,
            actionButtonText = R.string.block,
        )
    }
    if (isPhotoVisible) {
        OtherUserProfilePhotoDialog(
            imageUrl = user.imageUrl,
            onDismiss = { isPhotoVisible = false }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtherUserProfileScreenPreview() {
    ChatEaseTheme {
        Scaffold(topBar = {
            CommonTopBar(
                onBackClick = {}
            )
        }) { paddingValues ->
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileScreenContent(
                    paddingValues = paddingValues,
                    user = User(
                        uid = "1",
                        fullName = "Test Test",
                        email = "",
                        imageUrl = null,
                        status = UserPresenceStatus.ONLINE,
                        blockedUserIds = emptyList()
                    ),
                    isConnected = true,
                    onSendMessage = {},
                    isBlocked = false,
                    onUnblockClick = {},
                    onBlockClick = {},
                    groups = List(10) {
                        Group(
                            conversationId = it.toString(),
                            userIds = listOf("1", "2", "3"),
                            adminIds = listOf("1"),
                            visibleToUserIds = emptyList(),
                            ownerId = "1",
                            name = "Test Group",
                            imageUrl = null,
                            removedAtByUserId = mapOf(
                                "1" to System.currentTimeMillis()
                            )
                        )
                    },
                    onNavigateToMutualGroup = {},
                    mutualGroups = emptyList(),
                    onViewPhotoClick = {},
                    hasProfilePhoto = true,
                )
            }
        }
    }
}

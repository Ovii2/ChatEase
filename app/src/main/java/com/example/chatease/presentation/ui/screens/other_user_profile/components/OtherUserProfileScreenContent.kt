package com.example.chatease.presentation.ui.screens.other_user_profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar

@Composable
fun OtherUserProfileScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    user: User,
    isConnected: Boolean,
    onSendMessage: () -> Unit,
    isBlocked: Boolean,
    onUnblockClick: (String) -> Unit,
    onBlockClick: () -> Unit,
    groups: List<Group>,
    onNavigateToMutualGroup: (String) -> Unit,
    mutualGroups: List<Group>,
    onViewPhotoClick: () -> Unit,
    hasProfilePhoto: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OtherUserProfileTopSection(
                        user = user,
                        isConnected = isConnected,
                        onSendRequest = {},
                        onSendMessage = onSendMessage,
                        isBlocked = isBlocked,
                        onUnblockClick = onUnblockClick
                    )

                    if (!isBlocked) {
                        OtherUserProfileActionsSection(
                            onViewPhotoClick = onViewPhotoClick,
                            onBlockClick = onBlockClick,
                            onReportClick = {},
                            hasProfilePhoto = hasProfilePhoto,
                        )

                        OtherUserProfileAboutSection()

                        if (mutualGroups.isNotEmpty()) {
                            OtherUserProfileMutualGroupSection(
                                groups = groups,
                                onNavigateToMutualGroup = onNavigateToMutualGroup
                            )
                        }
                    }
                }
            }

            UserAvatar(
                user = user,
                avatarSize = 140.dp,
                statusBubbleSize = 35.dp,
                initialsFontSize = 70.sp,
                statusBubbleOffsetX = (-2).dp,
                statusBubbleOffsetY = (-4).dp,
                showStatus = isConnected && !isBlocked,
                hasBorder = true
            )
        }
    }
}
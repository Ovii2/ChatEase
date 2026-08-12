package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.about_section.ExtraPaneAboutSection
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.media_section.ExtraPaneMediaSection
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.more_section.ExtraPaneMoreSection
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.notifications_section.ExtraPaneNotificationsSection
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.top_section.ExtraPaneTopSection
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPane(
    modifier: Modifier = Modifier,
    user: User,
    onDeleteConversationClick: () -> Unit,
    isConversationCreator: Boolean,
    onBlockContactClick: (String) -> Unit,
    onUnblockContactClick: (String) -> Unit,
    isBlockedByMe: Boolean,
    isBlockedByOtherUser: Boolean,
    onViewContactClick: (String) -> Unit,
    currentUserId: String
) {
    var checked by rememberSaveable { mutableStateOf(false) }
    val iconSize = 26.dp
    val showProfileDetails = !isBlockedByMe && !isBlockedByOtherUser

    val mediaItems = List(2) { index ->
        MediaItem(
            id = index.toString(),
            thumbnailUrl = "https://picsum.photos/200/200?random=$index",
            mediaUrl = "https://picsum.photos/1200/1200?random=$index",
            type = MediaType.IMAGE
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .widthIn(max = 500.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtraPaneTopSection(
                user = user,
                showStatus = showProfileDetails,
                showQuickActions = showProfileDetails,
            )
            if (showProfileDetails) {
                ExtraPaneAboutSection()

            }
            if (mediaItems.isNotEmpty()) {
                ExtraPaneMediaSection(
                    mediaItems = mediaItems,
                    currentUserId = currentUserId
                )
            }
            if (showProfileDetails) {
                ExtraPaneNotificationsSection(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    iconSize = iconSize,
                )
            }
            ExtraPaneMoreSection(
                iconSize = iconSize,
                onViewContactClick = { onViewContactClick(user.uid) },
                onShareContactClick = {},
                onBlockContactClick = { onBlockContactClick(user.uid) },
                onUnblockContactClick = { onUnblockContactClick(user.uid) },
                onDeleteConversationClick = onDeleteConversationClick,
                isConversationCreator = isConversationCreator,
                isBlockedByMe = isBlockedByMe,
                isBlockedByOtherUser = isBlockedByOtherUser,
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ExtraPanePreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ExtraPane(
                    user = user,
                    onDeleteConversationClick = {},
                    isConversationCreator = true,
                    onBlockContactClick = {},
                    onUnblockContactClick = {},
                    isBlockedByMe = false,
                    isBlockedByOtherUser = false,
                    onViewContactClick = {},
                    currentUserId = "1"
                )
            }
        }
    }
}

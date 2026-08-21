package com.example.chatease.presentation.ui.screens.shared.panes.left_pane

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.ConversationUiModel
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.components.LeftPaneHeader
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.components.RecentChatsList
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LeftPane(
    modifier: Modifier = Modifier,
    user: User,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onConversationClick: (String, Boolean) -> Unit,
    onNavigateToMembershipScreen: () -> Unit,
    conversations: List<ConversationUiModel>,
    focusManager: FocusManager,
    onLogoutClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLongClick: (String, Boolean) -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    currentUserId: String
) {
    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus()
        }) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LeftPaneHeader(
                imageUrl = user.imageUrl,
                categories = categories,
                selectedCategory = selectedCategory,
                onSelectCategory = onSelectCategory,
                onLogoutClick = onLogoutClick,
                onNavigateToProfile = onNavigateToProfile,
                searchValue = searchValue,
                onSearchValueChange = onSearchValueChange,
            )
            RecentChatsList(
                conversations = conversations,
                onConversationClick = onConversationClick,
                onNavigateToMembershipScreen = onNavigateToMembershipScreen,
                onLongClick = onLongClick,
                currentUserId = currentUserId,
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun LeftPanePreview() {
    val user = User(
        uid = "123",
        fullName = "Test Testing",
        email = "test@email.com",
        imageUrl = "",
        status = UserPresenceStatus.AWAY
    )

    val categories = List(10) {
        Category(
            id = it.toString(),
            name = R.string.friends,
            icon = Icons.Outlined.Group
        )
    }

    val conversation = ConversationUiModel(
        conversationId = "1",
        title = "Test Test",
        imageUrl = null,
        participants = listOf(user),
        lastMessage = "",
        timestamp = System.currentTimeMillis(),
        unreadCount = 2,
        isGroup = false,
        isCurrentUserGroupMember = true,
        lastMessageType = MessageType.TEXT,
        isBlockedByOtherUser = false,
        categoryId = "1",
        lastMessageSenderId = "1",
    )
    ChatEaseTheme {
        Column(modifier = Modifier.systemBarsPadding()) {
            LeftPane(
                user = user,
                categories = categories,
                selectedCategory = "All",
                onSelectCategory = {},
                onConversationClick = { _, _ -> },
                onNavigateToMembershipScreen = {},
                conversations = List(4) { conversation },
                focusManager = LocalFocusManager.current,
                onLogoutClick = {},
                onNavigateToProfile = {},
                onLongClick = { _, _ -> },
                searchValue = "",
                onSearchValueChange = {},
                currentUserId = "1",
            )
        }
    }
}

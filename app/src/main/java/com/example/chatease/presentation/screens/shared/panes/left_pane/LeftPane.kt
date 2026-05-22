package com.example.chatease.presentation.screens.shared.panes.left_pane

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserStatus
import com.example.chatease.presentation.screens.shared.panes.left_pane.components.LeftPaneHeader
import com.example.chatease.presentation.screens.shared.panes.left_pane.components.RecentChatsList
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LeftPane(
    modifier: Modifier = Modifier,
    user: User,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onConversationClick: (String) -> Unit,
    onClickToSeeAll: () -> Unit,
    conversations: List<Conversation>,
    focusManager: FocusManager,
    onLogoutClick: () -> Unit
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
                onLogoutClick = onLogoutClick
            )
            RecentChatsList(
                conversations = conversations,
                onConversationClick = onConversationClick,
                onClickToSeeAll = onClickToSeeAll
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
        status = UserStatus.AWAY
    )

    val categories = listOf(
        Category(id = "all", name = "All"),
        Category(id = "work", name = "Work"),
        Category(id = "friends", name = "Friends")
    )

    val conversation = Conversation(
        id = "1",
        participants = listOf(user),
        lastMessage = "Hey! Are we still for lunch?",
        timestamp = System.currentTimeMillis(),
        unreadCount = 2
    )
    ChatEaseTheme() {
        Column(modifier = Modifier.systemBarsPadding()) {
            LeftPane(
                user = user,
                categories = categories,
                selectedCategory = "All",
                onSelectCategory = {},
                onConversationClick = {},
                onClickToSeeAll = {},
                conversations = listOf(conversation, conversation, conversation, conversation),
                focusManager = LocalFocusManager.current,
                onLogoutClick = {}
            )
        }
    }
}
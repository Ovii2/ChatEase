package com.example.chatease.presentation.screens.chats

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.screens.chats.components.panes.left_pane.LeftPane
import com.example.chatease.presentation.screens.components.chat.ChatBottomBar
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToCalls: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onConversationClick: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            ChatBottomBar(
                currentRoute = Screens.Home.route,
                onNavigateToHome = onNavigateToHome,
                onNavigateToContacts = onNavigateToContacts,
                onStartNewChat = {},
                onNavigateToCalls = onNavigateToCalls,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        LeftPane(
            modifier = modifier
                .padding(paddingValues)
                .systemBarsPadding(),
            user = User(),
            categories = listOf(),
            selectedCategory = "All",
            onSelectCategory = {},
            onConversationClick = onConversationClick,
            onClickToSeeAll = {},
            conversations = listOf()
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:width=411dp,height=891dp,orientation=landscape"
)
@Composable
private fun HomeScreenPreview() {
    val user = User(
        uid = "1",
        fullName = "Test test",
        email = "test@email.com",
        imageUrl = null,
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
        HomeScreen(
            onNavigateToHome = {},
            onNavigateToContacts = {},
            onNavigateToCalls = {},
            onNavigateToProfile = {},
            onConversationClick = {}
        )
    }
}
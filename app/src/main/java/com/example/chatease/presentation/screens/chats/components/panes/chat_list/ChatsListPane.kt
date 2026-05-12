package com.example.chatease.presentation.screens.chats.components.panes.chat_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.screens.chats.components.panes.chat_list.components.ChatListHeader
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ChatsListPane(
    modifier: Modifier = Modifier,
    user: User,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 12.dp)) {
        ChatListHeader(
            imageUrl = user.imageUrl,
            categories = categories,
            selectedCategory = selectedCategory,
            onSelectCategory = onSelectCategory,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatsListPanePreview() {
    val user = User(
        uid = "123",
        fullName = "Test Testing",
        email = "test@email.com",
        imageUrl = "https://image",
        status = UserStatus.ONLINE
    )

    val categories = listOf(
        Category(id = "all", name = "All"),
        Category(id = "work", name = "Work"),
        Category(id = "friends", name = "Friends")
    )
    ChatEaseTheme() {
        Column(modifier = Modifier.systemBarsPadding()) {
            ChatsListPane(
                user = user,
                categories = categories,
                selectedCategory = "All",
                onSelectCategory = {},
            )
        }
    }
}
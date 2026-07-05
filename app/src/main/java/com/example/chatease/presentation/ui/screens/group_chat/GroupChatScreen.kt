package com.example.chatease.presentation.ui.screens.group_chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupChatTopBar
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupMessageList
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val members = 2
    val group = Group(
        conversationId = "1",
        name = "New Groupppppppp",
        imageUrl = null
    )
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "email@test.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    val currentUserId = "1"

    fun generateRandomUserIds(count: Int): List<String> {
        return (1..10)
            .shuffled()
            .take(count)
            .map { "user_$it" }
    }

    val messages = List(3) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = listOf("user_1", "user_2").random(),
            text = LoremIpsum(1).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = generateRandomUserIds(10),
            reactions = emptyMap(),
            messageType = MessageType.TEXT
        )
    }
    val groupMembers = List(10) {
        User(
            uid = "user_$it",
            fullName = "Test Test",
            email = "test@email.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList(),

            )
    }
    val listState = rememberLazyListState()
    val firstUnreadMessageId = "1"

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            GroupChatTopBar(
                onBackClick = onBackClick,
                members = members,
                group = group
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
//                .imePadding()
//                .systemBarsPadding()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GroupMessageList(
                user = user,
                messages = messages,
                currentUserId = currentUserId,
                listState = listState,
                groupMembers = groupMembers,
                firstUnreadMessageId = firstUnreadMessageId
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatScreen(
                    onBackClick = {}
                )
            }
        }
    }
}

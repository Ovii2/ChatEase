package com.example.chatease.presentation.ui.screens.group_chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ChatBubble
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupMessageList(
    modifier: Modifier = Modifier,
    user: User,
    messages: List<Message>,
    isSentByCurrentUser: Boolean,
    listState: LazyListState,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(messages) { message ->
            GroupMessageListItem(
                user = user,
                message = message,
                isSentByCurrentUser = isSentByCurrentUser
            )
        }
    }
}

@Composable
fun GroupMessageListItem(
    modifier: Modifier = Modifier,
    user: User,
    message: Message,
    isSentByCurrentUser: Boolean
) {
    val fullName = user.fullName
    val name = fullName.split(" ")[0]
    val lastname = fullName.split(" ")[1]
    val lastnameInitial = lastname.first()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        UserAvatar(
            user = user,
            avatarSize = 50.dp,
            statusBubbleSize = 14.dp,
            initialsFontSize = 20.sp,
        )
        Column {
            if (!isSentByCurrentUser) {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "$name $lastnameInitial.",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            ChatBubble(
                message = message,
                isSentByCurrentUser = isSentByCurrentUser,
                currentUserId = "1",
                showReactions = false,
                isFirstInGroup = false,
                isMiddleInGroup = false,
                isLastInGroup = false,
                onLongClick = {},
                onDismissReactions = {},
                onReactionClick = { _, _ -> }
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "List item")
@Composable
private fun GroupMessageListItemPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    val message = Message(
        messageId = "1",
        conversationId = "1",
        senderId = "1",
        text = LoremIpsum(20).values.first(),
        timeStamp = System.currentTimeMillis(),
        seenBy = listOf(""),
        reactions = emptyMap(),
        messageType = MessageType.TEXT
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupMessageListItem(
                    user = user,
                    message = message,
                    isSentByCurrentUser = false,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "List")
@Composable
private fun GroupMessageListPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    val message = List(10) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = listOf("1", "2").random(),
            text = LoremIpsum(20).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf(""),
            reactions = emptyMap(),
            messageType = MessageType.TEXT
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupMessageList(
                    user = user,
                    messages = message,
                    isSentByCurrentUser = false,
                    listState = rememberLazyListState(),
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MessagesList(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    currentUserId: String,
    user: User,
    listState: LazyListState
) {
    val firstUnreadMessageId = "3"

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(messages) { message ->
            val isSentByCurrentUser = message.senderId == currentUserId
            if (message.messageId == firstUnreadMessageId) {
                UnreadMessagesDivider()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isSentByCurrentUser) {
                        UserAvatar(
                            user = user,
                            avatarSize = 50.dp,
                            statusBubbleSize = 14.dp,
                            initialsFontSize = 20.sp
                        )
                    } else Unit
                    ChatBubble(
                        message = message,
                        isSentByCurrentUser = isSentByCurrentUser
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MessagesListPreview() {
    val messages = listOf(
        Message(
            messageId = "1",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Hey! Are we still for lunch?",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_2")
        ),
        Message(
            messageId = "2",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "I was thinking maybe we could also stop by that new café near the park afterwards. I heard they have really good desserts and coffee there 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "3",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "4",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        )
    )

    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    ChatEaseTheme() {
        Column(modifier = Modifier.systemBarsPadding()) {
            MessagesList(
                messages = messages,
                currentUserId = "user_2",
                user = user,
                listState = rememberLazyListState(),
            )
        }
    }
}


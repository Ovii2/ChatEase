package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.CommonChip
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.isSameDay
import com.example.chatease.utils.toChatDateLabel

@Composable
fun MessagesList(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    currentUserId: String,
    user: User,
    listState: LazyListState,
    firstUnreadMessageId: String?,
    onReactionClick: (String, String) -> Unit
) {
    var selectedReactionMessageId by rememberSaveable { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            selectedReactionMessageId = null
            focusManager.clearFocus()
        }
    ) {
        val reversedMessages = messages.reversed()

        LazyColumn(
            modifier = Modifier,
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(reversedMessages) { index, message ->
                val nextMessage = reversedMessages.getOrNull(index + 1)

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
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
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
                            isSentByCurrentUser = isSentByCurrentUser,
                            currentUserId = currentUserId,
                            showReactions = message.messageId == selectedReactionMessageId,
                            onLongClick = { selectedReactionMessageId = message.messageId },
                            onDismissReactions = { selectedReactionMessageId = null },
                            onReactionClick = { messageId, reaction ->
                                onReactionClick(messageId, reaction)
                            },
                        )
                    }
                }
                if (nextMessage == null || !isSameDay(
                        message.timeStamp,
                        nextMessage.timeStamp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CommonChip(
                            text = message.timeStamp.toChatDateLabel(context),
                            selected = false,
                            enabled = false
                        )
                    }
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
            text = LoremIpsum(50).values.first(),
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
                firstUnreadMessageId = "1",
                onReactionClick = { _, _ -> },
            )
        }
    }
}


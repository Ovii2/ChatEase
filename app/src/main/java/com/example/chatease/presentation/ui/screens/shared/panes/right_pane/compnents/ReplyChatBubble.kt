package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.screens.shared.chat.ReplyBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReplyChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isSentByCurrentUser: Boolean,
    currentUserId: String,
    showReactions: Boolean,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onRemoveReactionClick: (String, String) -> Unit,
    conversationType: ConversationType = ConversationType.DIRECT,
    onShowUsersReactionsClick: (String) -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserMemberOfGroup: Boolean,
    messageSenderName: String,
    onReplyPreviewClick: () -> Unit,
    repliedMessageSenderName: String
) {
    ReplyBubbleContainer(
        modifier = modifier,
        isSentByCurrentUser = isSentByCurrentUser,
        message = message,
        currentUserId = currentUserId,
        messageSenderName = messageSenderName,
        repliedMessageSenderName = repliedMessageSenderName,
        replyPreview = {
            Surface(
                modifier = Modifier
                    .offset(
                        x = if (isSentByCurrentUser) (-8).dp else 12.dp,
                        y = 10.dp
                    )
                    .clickable(onClick = onReplyPreviewClick),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = if (message.replyMessage?.messageType == MessageType.FILE) {
                        message.replyMessage.fileName
                    } else {
                        message.replyMessage?.text.orEmpty()
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    ) {
        ChatBubble(
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            currentUserId = currentUserId,
            showReactions = showReactions,
            isFirstInGroup = false,
            isMiddleInGroup = false,
            isLastInGroup = false,
            onLongClick = onLongClick,
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            conversationType = conversationType,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup,
            isReplyMessage = true,
            onRemoveReactionClick = onRemoveReactionClick
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReplyChatBubblePreview() {
    val message = Message(
        messageId = "1",
        conversationId = "2",
        senderId = "1",
        text = LoremIpsum(12).values.first(),
        timeStamp = System.currentTimeMillis(),
        seenBy = listOf("user_1"),
        reactions = mapOf(
            "user_1" to "\uD83E\uDD70"
        )
//        reactions = emptyMap()
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReplyChatBubble(
                    message = message,
                    isSentByCurrentUser = false,
                    currentUserId = "1",
                    showReactions = false,
                    onLongClick = {},
                    onDismissReactions = {},
                    onReactionClick = { _, _ -> },
                    onRemoveReactionClick = { _, _ -> },
                    conversationType = ConversationType.DIRECT,
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true,
                    messageSenderName = "Test",
                    onReplyPreviewClick = {},
                    repliedMessageSenderName = "User",
                )
            }
        }
    }
}
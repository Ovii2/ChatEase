package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.screens.shared.chat.MessageBubbleContainer
import com.example.chatease.presentation.ui.screens.shared.chat.ReplyBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReplyImageBubble(
    modifier: Modifier = Modifier,
    isSentByCurrentUser: Boolean,
    message: Message,
    showReactions: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onShowUsersReactionsClick: (String) -> Unit,
    onRemoveReactionClick: (String, String) -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserMemberOfGroup: Boolean,
    currentUserId: String,
    messageSenderName: String,
    onReplyPreviewClick: () -> Unit,
    repliedMessageSenderName: String
) {
    val textColor = if (isSentByCurrentUser) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    ReplyBubbleContainer(
        modifier = modifier,
        isSentByCurrentUser = isSentByCurrentUser,
        message = message,
        currentUserId = currentUserId,
        messageSenderName = messageSenderName,
        repliedMessageSenderName = repliedMessageSenderName,
        replyPreview = {
            AsyncImage(
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .alpha(0.4f)
                    .clickable(onClick = onReplyPreviewClick),
                model = message.replyMessage?.imageUrl.orEmpty(),
                contentDescription = null
            )
        }
    ) {
        MessageBubbleContainer(
            modifier = Modifier.offset(y = (-6).dp),
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            showReactions = showReactions,
            onClick = onClick,
            onLongClick = onLongClick,
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            onRemoveReactionClick = onRemoveReactionClick,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup,
            isReplyMessage = true,
            currentUserId = currentUserId
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = message.text,
                color = textColor
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReplyImageBubblePreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReplyImageBubble(
                    isSentByCurrentUser = false,
                    message = Message(
                        messageId = "1",
                        conversationId = "1",
                        senderId = "1",
                        text = "Test message",
                        timeStamp = System.currentTimeMillis(),
                        seenBy = listOf("1", "2"),
                        reactions = emptyMap(),
                        messageType = MessageType.IMAGE,
                        replyMessage = ReplyMessage(
                            messageId = "1",
                            senderId = "1",
                            text = "Replying"
                        )
                    ),
                    showReactions = false,
                    onClick = {},
                    onLongClick = {},
                    onDismissReactions = {},
                    onReactionClick = { _, _ -> },
                    onShowUsersReactionsClick = {},
                    onRemoveReactionClick = { _, _ -> },
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true,
                    currentUserId = "1",
                    messageSenderName = "Test Tester",
                    onReplyPreviewClick = {},
                    repliedMessageSenderName = "Test"
                )
            }
        }
    }
}
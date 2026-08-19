package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.screens.shared.chat.MessageBubbleContainer
import com.example.chatease.presentation.ui.screens.shared.chat.ReplyBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReplyFileBubble(
    modifier: Modifier = Modifier,
    filename: String,
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
        modifier = modifier.widthIn(max = 300.dp),
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
                        y = (10).dp
                    )
                    .clickable(onClick = onReplyPreviewClick),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = filename,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    ) {
        MessageBubbleContainer(
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            showReactions = showReactions,
            onClick = onClick,
            onLongClick = onLongClick,
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup,
            isReplyMessage = true,
            currentUserId = currentUserId,
            onRemoveReactionClick = onRemoveReactionClick,
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
private fun ReplyFileBubblePreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReplyFileBubble(
                    isSentByCurrentUser = true,
                    filename = "test_chat_document_2043434340_23123123.pdf",
                    message = Message(
                        messageId = "1",
                        conversationId = "1",
                        senderId = "1",
                        text = "Test message",
                        timeStamp = System.currentTimeMillis(),
                        seenBy = listOf("1", "2"),
                        reactions = emptyMap(),
                        messageType = MessageType.FILE,
                        replyMessage = ReplyMessage(
                            messageId = "1",
                            senderId = "1",
                            text = "Replying"
                        ),
                        fileAttachments = listOf(
                            FileAttachment(
                                id = "1",
                                name = "test_chat_document_2043434340_23123123.pdf",
                                size = 12345L,
                                url = "",
                                mimeType = ""
                            )
                        )
                    ),
                    currentUserId = "1",
                    messageSenderName = "Tester",
                    repliedMessageSenderName = "Test",
                    showReactions = false,
                    onClick = {},
                    onLongClick = {},
                    onDismissReactions = {},
                    onReactionClick = { _, _ -> },
                    onRemoveReactionClick = { _, _ -> },
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    onReplyPreviewClick = {},
                    isUserMemberOfGroup = false,
                )
            }
        }
    }
}
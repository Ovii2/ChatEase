package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReplyBubbleContainer(
    modifier: Modifier = Modifier,
    isSentByCurrentUser: Boolean,
    message: Message,
    currentUserId: String,
    messageSenderName: String,
    repliedMessageSenderName: String,
    replyPreview: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val replied = when {
        isSentByCurrentUser &&
                message.replyMessage?.senderId == currentUserId -> {
            stringResource(R.string.you_replied_to_yourself)
        }

        else -> {
            stringResource(
                R.string.other_user_replied_to_someone,
                messageSenderName,
                repliedMessageSenderName
            )
        }
    }

    Column(
        modifier = modifier
            .widthIn(max = 250.dp)
            .offset(
                y = if (!isSentByCurrentUser) 10.dp else 0.dp,
                x = if (!isSentByCurrentUser) 4.dp else 0.dp
            ),
        horizontalAlignment = if (isSentByCurrentUser) {
            Alignment.End
        } else {
            Alignment.Start
        }
    ) {
        Row(
            modifier = Modifier.offset(
                x = if (isSentByCurrentUser) (-20).dp else 20.dp,
                y = 5.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

            if (isSentByCurrentUser) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = tint
                )
                Text(
                    text = replied,
                    style = MaterialTheme.typography.labelLarge,
                    color = tint
                )
            } else {
                Text(
                    text = replied,
                    style = MaterialTheme.typography.labelLarge,
                    color = tint
                )
                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(
                            scaleX = -1f,
                            scaleY = 1f
                        ),
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = tint
                )
            }
        }
        replyPreview()
        content()
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReplyBubbleContainerPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReplyBubbleContainer(
                    isSentByCurrentUser = true,
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
                        ),
                        fileAttachments = listOf(
                            FileAttachment(
                                id = "1",
                                name = "File",
                                size = 12345L,
                                url = "",
                                mimeType = ""
                            )
                        )
                    ),
                    currentUserId = "1",
                    messageSenderName = "Tester",
                    repliedMessageSenderName = "Test",
                    replyPreview = {},
                    content = {}
                )
            }
        }
    }
}
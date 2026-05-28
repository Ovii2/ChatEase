package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Message
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight
import com.example.chatease.utils.toChatTimeStamp

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isSentByCurrentUser: Boolean
) {
    val backgroundColor =
        if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh

    val shape =
        if (isSentByCurrentUser) {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 4.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            )
        }

    val seenCheckColor = if (isSystemInDarkTheme()) successGreenDark else successGreenLight

    Row(verticalAlignment = Alignment.Bottom) {
        Surface(
            color = backgroundColor,
            shape = shape
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .padding(12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.timeStamp.toChatTimeStamp(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSentByCurrentUser) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        }
                    )
                    if (isSentByCurrentUser) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = if (message.seenBy.isNotEmpty()) {
                                Icons.Outlined.DoneAll
                            } else {
                                Icons.Outlined.Check
                            },
                            contentDescription = null,
                            tint = if (message.seenBy.isNotEmpty()) {
                                seenCheckColor
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun ChatBubblePreview() {
    val message = Message(
        messageId = "1",
        conversationId = "2",
        senderId = "1",
        text = "Hey! Are we still for lunch?",
        timeStamp = System.currentTimeMillis(),
        seenBy = listOf("user_1")
    )
    ChatEaseTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ChatBubble(
                message = message,
                isSentByCurrentUser = true,
            )
        }
    }
}

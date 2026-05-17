package com.example.chatease.presentation.screens.chats.components.panes.right_pane

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Message
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toChatTimeStamp

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isSentByCurrentUser: Boolean
) {
    val backgroundColor =
        if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh

    Row(verticalAlignment = Alignment.Bottom) {
        if (!isSentByCurrentUser) {
            MessageTail(
                backgroundColor = backgroundColor,
                rotation = -100f,
                offsetX = 9.dp,
                offsetY = 1.dp
            )
        }

        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(16.dp)
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
                    if (isSentByCurrentUser && message.seenBy.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.size(13.dp),
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        if (isSentByCurrentUser) {
            MessageTail(
                backgroundColor = backgroundColor,
                rotation = 145f,
                offsetX = (-13).dp,
                offsetY = 12.dp
            )
        }
    }
}

@Composable
private fun MessageTail(
    backgroundColor: Color,
    rotation: Float,
    offsetX: Dp,
    offsetY: Dp
) {
    Canvas(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .rotate(rotation)
            .size(width = 20.dp, height = 21.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }

        drawPath(
            path = path,
            color = backgroundColor
        )
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
    ChatEaseTheme() {
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

package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight
import com.example.chatease.utils.toChatBubbleTimeStamp

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isSentByCurrentUser: Boolean,
    currentUserId: String,
    showReactions: Boolean,
    isFirstInGroup: Boolean,
    isMiddleInGroup: Boolean,
    isLastInGroup: Boolean,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    conversationType : ConversationType = ConversationType.DIRECT
) {
    val backgroundColor =
        if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val isSeenByOtherUser = message.seenBy.any { userId -> userId != currentUserId }

    val largeCorner = 18.dp
    val smallCorner = 6.dp
    val isSingleMessage = isFirstInGroup && isLastInGroup

    val topEnd = if (isLastInGroup || isMiddleInGroup) smallCorner else largeCorner
    val bottomEnd = if (isFirstInGroup || isMiddleInGroup) smallCorner else largeCorner

    val shape = if (isSingleMessage && isSentByCurrentUser) {
        RoundedCornerShape(
            topStart = largeCorner,
            topEnd = largeCorner,
            bottomStart = largeCorner,
            bottomEnd = smallCorner
        )
    } else if (isSingleMessage) {
        RoundedCornerShape(
            topStart = largeCorner,
            topEnd = largeCorner,
            bottomStart = smallCorner,
            bottomEnd = largeCorner
        )
    } else {

        if (isSentByCurrentUser) {
            RoundedCornerShape(
                topStart = largeCorner,
                topEnd = topEnd,
                bottomStart = largeCorner,
                bottomEnd = bottomEnd
            )
        } else {
            RoundedCornerShape(
                topStart = topEnd,
                topEnd = largeCorner,
                bottomStart = bottomEnd,
                bottomEnd = largeCorner
            )
        }
    }


    val seenCheckColor = if (isSystemInDarkTheme()) successGreenDark else successGreenLight
    val reactionBadgeAlignment = if (isSentByCurrentUser) Alignment.BottomEnd else
        Alignment.BottomStart

    val reactionBadgeOffset =
        if (isSentByCurrentUser) IntOffset(x = -10, y = 60) else IntOffset(x = 30, y = 55)

    val popUpAlignment = if (isSentByCurrentUser) Alignment.TopEnd else Alignment.TopStart
    val textColor =
        if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface


    Row(
        modifier = Modifier.padding(
            start = 12.dp,
            top = 0.dp,
            end = 4.dp,
            bottom = 0.dp
        ),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            Surface(
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        onLongClick()
                    }
                ),
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
                            text = message.timeStamp.toChatBubbleTimeStamp(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSentByCurrentUser) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            }
                        )
                        if (isSentByCurrentUser && conversationType == ConversationType.DIRECT) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = if (isSeenByOtherUser) {
                                    Icons.Outlined.DoneAll
                                } else {
                                    Icons.Outlined.Check
                                },
                                contentDescription = null,
                                tint = if (isSeenByOtherUser) {
                                    seenCheckColor
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        }
                    }
                }
            }
            if (message.reactions.isNotEmpty()) {
                ReactionBadge(
                    modifier = Modifier
                        .align(reactionBadgeAlignment)
                        .offset { reactionBadgeOffset }
                        .padding(bottom = 6.dp),
                    reactionCounts = message.reactions.values.groupingBy { it }.eachCount(),
                    backGroundColor = backgroundColor,
                    textColor = textColor,
                )
            }
            if (showReactions) {
                Popup(
                    alignment = popUpAlignment,
                    offset = IntOffset(0, -160)
                ) {
                    ChatReactionRow(
                        onReactionClick = { reaction ->
                            onReactionClick(
                                message.messageId,
                                reaction
                            )
                        }
                    )
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
        seenBy = listOf("user_1"),
        reactions = mapOf(
            "user_1" to "\uD83E\uDD70"
        )
    )
    ChatEaseTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ChatBubble(
                message = message,
                isSentByCurrentUser = false,
                currentUserId = "1",
                showReactions = true,
                onLongClick = {},
                onDismissReactions = {},
                onReactionClick = { _, _ -> },
                isFirstInGroup = true,
                isMiddleInGroup = true,
                isLastInGroup = true,
                conversationType = ConversationType.DIRECT
            )
        }
    }
}

package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.screens.shared.chat.ReplyBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReplyMultipleImageChatBubble(
    modifier: Modifier = Modifier,
    isSentByCurrentUser: Boolean,
    message: Message,
    showReactions: Boolean,
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
    repliedMessageSenderName: String,
    imageCount: Int
) {
    ReplyBubbleContainer(
        modifier = modifier.padding(top = 4.dp),
        isSentByCurrentUser = isSentByCurrentUser,
        message = message,
        currentUserId = currentUserId,
        messageSenderName = messageSenderName,
        repliedMessageSenderName = repliedMessageSenderName,
        replyPreview = {
            Surface(
                modifier = modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) { onReplyPreviewClick() }
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(15.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.total_images, imageCount),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) {
        ChatBubble(
            modifier = Modifier.offset(y = (-6).dp),
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            currentUserId = currentUserId,
            showReactions = showReactions,
            isFirstInGroup = true,
            isMiddleInGroup = false,
            isLastInGroup = false,
            onLongClick = onLongClick,
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            onRemoveReactionClick = onRemoveReactionClick,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReplyMultipleImageChatBubblePreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReplyMultipleImageChatBubble(
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
                        )
                    ),
                    showReactions = false,
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
                    repliedMessageSenderName = "Test",
                    imageCount = 2
                )
            }
        }
    }
}
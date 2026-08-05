package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.enums.ConversationType
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
    conversationType: ConversationType = ConversationType.DIRECT,
    onShowUsersReactionsClick: (String) -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserMemberOfGroup: Boolean,
    messageSenderName: String,
    repliedMessageSenderName: String
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
        modifier = modifier,
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
        Surface(
            modifier = Modifier.offset(
                x = if (isSentByCurrentUser) (-8).dp else 12.dp,
                y = (10).dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = message.replyMessage?.text.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
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
            isReplyMessage = true
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
                    conversationType = ConversationType.DIRECT,
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true,
                    messageSenderName = "Test",
                    repliedMessageSenderName = "User",
                )
            }
        }
    }
}
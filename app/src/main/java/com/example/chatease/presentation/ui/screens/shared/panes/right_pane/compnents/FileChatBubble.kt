package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.presentation.ui.screens.shared.chat.MessageBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toFormattedFileSize
import com.example.chatease.utils.toTruncatedFileName

@Composable
fun FileChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    filename: String,
    onForwardClick: () -> Unit,
    isSentByCurrentUser: Boolean,
    showReactions: Boolean,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onShowUsersReactionsClick: (String) -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserMemberOfGroup: Boolean
) {
    val textColor = if (isSentByCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val iconTint = if (isSentByCurrentUser) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.scrim
    }

    val fileBackgroundColor = if (isSentByCurrentUser) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSentByCurrentUser) {
                FileForwardArrow(
                    onForwardClick = onForwardClick,
                    isSentByCurrentUser = true,
                    iconTint = iconTint
                )
            }

            MessageBubbleContainer(
                modifier = Modifier.weight(1f),
                message = message,
                isSentByCurrentUser = isSentByCurrentUser,
                showReactions = showReactions,
                isFirstInGroup = true,
                isMiddleInGroup = false,
                isLastInGroup = false,
                shapeOverride = RoundedCornerShape(10.dp),
                onLongClick = onLongClick,
                onDismissReactions = onDismissReactions,
                onReactionClick = onReactionClick,
                onShowUsersReactionsClick = onShowUsersReactionsClick,
                isBlockedByOtherUser = isBlockedByOtherUser,
                isUserMemberOfGroup = isUserMemberOfGroup
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = fileBackgroundColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sharp_draft_24),
                            contentDescription = null,
                            tint = iconTint
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = filename.toTruncatedFileName(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.W600
                        )

                        Text(
                            text = message.fileAttachment
                                ?.size
                                ?.toFormattedFileSize()
                                ?: "0 B",
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                            fontWeight = FontWeight.W400
                        )
                    }
                }
            }

            if (!isSentByCurrentUser) {
                FileForwardArrow(
                    onForwardClick = onForwardClick,
                    isSentByCurrentUser = false,
                    iconTint = iconTint
                )
            }
        }
    }
}

@Composable
fun FileForwardArrow(
    modifier: Modifier = Modifier,
    onForwardClick: () -> Unit,
    isSentByCurrentUser: Boolean,
    iconTint: Color
) {
    val backgroundColor = if (isSentByCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Row(
        modifier = modifier.widthIn(max = 320.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onForwardClick)
                .size(35.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.scale(
                    scaleX = if (isSentByCurrentUser) -1f else 1f,
                    scaleY = 1f
                ),
                tint = iconTint,
                imageVector = Icons.AutoMirrored.Filled.Reply,
                contentDescription = null
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FileChatBubblePreview() {
    val message = Message(
        messageId = "1",
        senderId = "1",
        fileAttachment = FileAttachment(
            name = "",
            size = 1233445L,
            url = "",
            mimeType = ""
        ),
        reactions = mapOf(
            "1" to "\uD83E\uDD70"
        )
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
                FileChatBubble(
                    message = message,
                    filename = "Project_document_final_version_really_really_long_name.pdf",
                    onForwardClick = {},
                    isSentByCurrentUser = true,
                    showReactions = true,
                    onLongClick = {},
                    onDismissReactions = {},
                    onReactionClick = { _, _ -> },
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true
                )
            }
        }
    }
}

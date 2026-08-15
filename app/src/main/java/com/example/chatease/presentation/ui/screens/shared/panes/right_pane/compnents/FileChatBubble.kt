package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.chatease.presentation.ui.theme.darkLavender
import com.example.chatease.presentation.ui.theme.lightLavender
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
    isUserMemberOfGroup: Boolean,
    onFileClick: (Message) -> Unit,
    uploadingFileId: String?,
    fileUploadProgress: Float?
) {
    val backgroundColor = if (isSentByCurrentUser) {
        if (isSystemInDarkTheme()) darkLavender else lightLavender
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val boxColor = if (isSentByCurrentUser) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f)
    }

    val isUploadingThisFile = uploadingFileId == message.fileAttachment?.id

    MediaBubbleContainer(
        modifier = modifier,
        isSentByCurrentUser = isSentByCurrentUser,
        onForwardClick = onForwardClick
    ) {
        MessageBubbleContainer(
            modifier = Modifier
                .weight(1f),
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            showReactions = showReactions,
            isFirstInGroup = true,
            isMiddleInGroup = false,
            isLastInGroup = false,
            shapeOverride = RoundedCornerShape(10.dp),
            onLongClick = onLongClick,
            onClick = { onFileClick(message) },
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup,
            backgroundColorOverride = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = boxColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sharp_draft_24),
                        contentDescription = null
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
                        fontWeight = FontWeight.W400
                    )
                    if (isUploadingThisFile && fileUploadProgress != null) {
                        LinearProgressIndicator(
                            modifier = Modifier.padding(bottom = 2.dp),
                            progress = { fileUploadProgress }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MediaForwardArrow(
    modifier: Modifier = Modifier,
    onForwardClick: () -> Unit,
    isSentByCurrentUser: Boolean,
    backGroundColor: Color
) {
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
                    color = backGroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.scale(
                    scaleX = if (isSentByCurrentUser) -1f else 1f,
                    scaleY = 1f
                ),
                imageVector = Icons.AutoMirrored.Filled.Reply,
                contentDescription = null
            )
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
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
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {},
                    message = message,
                    filename = "short_title.pdf",
                    onForwardClick = {},
                    isSentByCurrentUser = true,
                    showReactions = true,
                    onLongClick = {},
                    onDismissReactions = {},
                    onReactionClick = { _, _ -> },
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true,
                    onFileClick = {},
                    uploadingFileId = message.fileAttachment?.id,
                    fileUploadProgress = 0.5f,
                )
            }
        }
    }
}

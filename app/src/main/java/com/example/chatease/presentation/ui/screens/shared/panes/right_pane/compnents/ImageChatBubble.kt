package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import coil3.compose.AsyncImage
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.screens.shared.chat.MessageBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ImageChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isSentByCurrentUser: Boolean,
    showReactions: Boolean,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onShowUsersReactionsClick: (String) -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserMemberOfGroup: Boolean,
    onImageClick: (FileAttachment) -> Unit,
    onForwardClick: () -> Unit
) {
    val attachment = message.fileAttachments.firstOrNull()
    val imageUrl = attachment?.url.orEmpty()

    MediaBubbleContainer(
        isSentByCurrentUser = isSentByCurrentUser,
        onForwardClick = onForwardClick
    ) {
        MessageBubbleContainer(
            modifier = modifier
                .weight(1f),
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            showReactions = showReactions,
            onLongClick = onLongClick,
            onClick = { attachment?.let(onImageClick) },
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup,
            isReplyMessage = false
        ) {
            AsyncImage(
                modifier = Modifier.aspectRatio(2f / 3f),
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
//            Image(
//                modifier = Modifier.aspectRatio(2f / 3f),
//                painter = painterResource(R.drawable.person),
//                contentDescription = null,
//                contentScale = ContentScale.Crop
//            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ImageChatBubblePreview() {
    val message = Message(
        messageId = "1",
        conversationId = "1",
        senderId = "1",
        text = LoremIpsum(20).values.first(),
        timeStamp = System.currentTimeMillis(),
        seenBy = emptyList(),
        reactions = emptyMap(),
        messageType = MessageType.IMAGE,
        replyMessage = ReplyMessage(
            messageId = "1",
            senderId = "1",
            text = LoremIpsum(10).values.first(),
            messageType = MessageType.TEXT,
            fileName = "file.pdf"
        ),
        fileAttachments = listOf(
            FileAttachment(
                id = "1",
                name = "file.pd",
                size = 123456L,
                url = "",
                mimeType = ""
            )
        )
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageChatBubble(
                    message = message,
                    isSentByCurrentUser = true,
                    showReactions = false,
                    onLongClick = {},
                    onDismissReactions = { },
                    onReactionClick = { _, _ -> },
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true,
                    onImageClick = {},
                    onForwardClick = {},
                )
            }
        }
    }
}

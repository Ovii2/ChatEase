package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.screens.shared.chat.MessageBubbleContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.darkLavender
import com.example.chatease.presentation.ui.theme.lightLavender

@Composable
fun MultipleImageChatBubble(
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
    onImageClick: (Message) -> Unit,
    onForwardClick: () -> Unit,
) {

    val backgroundColor = if (isSentByCurrentUser) {
        if (isSystemInDarkTheme()) darkLavender else lightLavender
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    MediaBubbleContainer(
        isSentByCurrentUser = isSentByCurrentUser,
        onForwardClick = onForwardClick
    ) {
        MessageBubbleContainer(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(
                    start = 0.dp,
                    top = 10.dp,
                    end = 7.dp,
                    bottom = 10.dp
                ),
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            showReactions = showReactions,
            onLongClick = onLongClick,
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick,
            onShowUsersReactionsClick = onShowUsersReactionsClick,
            isBlockedByOtherUser = isBlockedByOtherUser,
            isUserMemberOfGroup = isUserMemberOfGroup
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(10.dp)
                    ),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(10) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f),
                        painter = painterResource(R.drawable.person),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

//@Composable
//fun MultipleImageItem(modifier: Modifier = Modifier) {
//
//}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MultipleImageChatBubblePreview() {
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
        fileAttachment = FileAttachment(
            id = "1",
            name = "file.pd",
            size = 123456L,
            url = "",
            mimeType = ""
        )
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MultipleImageChatBubble(
                    message = message,
                    isSentByCurrentUser = false,
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
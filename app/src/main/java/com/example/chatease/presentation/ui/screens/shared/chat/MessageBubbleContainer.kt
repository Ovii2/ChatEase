package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.chatease.domain.model.Message
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ChatReactionRow
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ReactionBadge
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MessageBubbleContainer(
    modifier: Modifier = Modifier,
    message: Message,
    isSentByCurrentUser: Boolean,
    showReactions: Boolean,
    isFirstInGroup: Boolean = false,
    isMiddleInGroup: Boolean = false,
    isLastInGroup: Boolean = false,
    onLongClick: () -> Unit,
    onClick: () -> Unit = {},
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onShowUsersReactionsClick: (String) -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserMemberOfGroup: Boolean,
    isReplyMessage: Boolean = false,
    shapeOverride: Shape? = null,
    backgroundColorOverride: Color? = null,
    content: @Composable () -> Unit
) {
    val backgroundColor = backgroundColorOverride
        ?: if (isSentByCurrentUser) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }

    val textColor =
        if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

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

    val reactionBadgeAlignment =
        if (isSentByCurrentUser) Alignment.BottomEnd else Alignment.BottomStart
    val reactionBadgeOffset =
        if (isSentByCurrentUser) IntOffset(x = -10, y = 60) else IntOffset(x = 30, y = 55)
    val popUpAlignment = if (isSentByCurrentUser) Alignment.TopEnd else Alignment.TopStart


    Box(
        modifier = modifier.padding(
            start = 12.dp,
            end = 4.dp
        ),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
            color = backgroundColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            shape = shapeOverride ?: if (isReplyMessage) RoundedCornerShape(18.dp) else shape
        ) {
            content()
        }

        if (message.reactions.isNotEmpty()) {
            ReactionBadge(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onShowUsersReactionsClick(message.messageId)
                    }
                    .align(reactionBadgeAlignment)
                    .offset { reactionBadgeOffset }
                    .padding(bottom = 6.dp),
                reactionCounts = message.reactions.values
                    .groupingBy { it }
                    .eachCount(),
                backGroundColor = backgroundColor,
                textColor = textColor
            )
        }

        if (
            showReactions &&
            !isBlockedByOtherUser &&
            isUserMemberOfGroup
        ) {
            Popup(
                alignment = popUpAlignment,
                offset = IntOffset(0, -160),
                onDismissRequest = onDismissReactions,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    clippingEnabled = false,
                    usePlatformDefaultWidth = false
                )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MessageBubbleContainerPreview() {
    val message = Message(
        messageId = "1",
        senderId = "1",
        reactions = mapOf(
            "user_1" to "\uD83E\uDD70"
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
                MessageBubbleContainer(
                    message = message,
                    isSentByCurrentUser = true,
                    showReactions = true,
                    isFirstInGroup = true,
                    isMiddleInGroup = false,
                    isLastInGroup = true,
                    onLongClick = {},
                    onDismissReactions = {},
                    onReactionClick = { _, _ -> },
                    onShowUsersReactionsClick = {},
                    isBlockedByOtherUser = false,
                    isUserMemberOfGroup = true
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "Test message"
                    )
                }
            }
        }
    }
}
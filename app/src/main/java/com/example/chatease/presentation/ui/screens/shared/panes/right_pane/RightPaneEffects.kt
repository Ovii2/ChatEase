package com.example.chatease.presentation.ui.screens.shared.panes.right_pane

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.chatease.domain.model.Message
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RightPaneEffects(
    messages: List<Message>,
    firstUnreadMessageId: String?,
    currentUserId: String,
    listState: LazyListState,
    isNearBottom: Boolean,
    isUserDragging: Boolean,
    hasInitialScrollDone: Boolean,
    previousMessageCount: Int,
    shouldShowUnreadDivider: Boolean,
    hasUserScrolledAfterOpen: Boolean,
    onInitialUnreadMessageIdChange: (String?) -> Unit,
    onHasInitialScrollDoneChange: (Boolean) -> Unit,
    onNewMessageCountChange: (Int) -> Unit,
    onNewMessageCountIncrease: (Int) -> Unit,
    onPreviousMessageCountChange: (Int) -> Unit,
    onHasUserScrolledAfterOpenChange: (Boolean) -> Unit,
    onShouldShowUnreadDividerChange: (Boolean) -> Unit,
    onMessagesVisible: () -> Unit
) {
    val firstIndex = 0

    LaunchedEffect(messages.size, firstUnreadMessageId) {
        if (messages.isNotEmpty() && !hasInitialScrollDone && firstUnreadMessageId != null) {
            onInitialUnreadMessageIdChange(firstUnreadMessageId)

            val unreadIndex = messages.reversed().indexOfFirst { message ->
                message.messageId == firstUnreadMessageId
            }

            if (unreadIndex != -1) {
                onShouldShowUnreadDividerChange(true)
                listState.scrollToItem(unreadIndex)
            } else {
                listState.scrollToItem(firstIndex)
            }

            onHasInitialScrollDoneChange(true)
            onNewMessageCountChange(0)
            onPreviousMessageCountChange(messages.size)
        }
    }

    LaunchedEffect(messages.size) {
        val latestMessage = messages.lastOrNull()

        if (
            messages.size > previousMessageCount &&
            !isNearBottom &&
            latestMessage?.senderId != currentUserId
        ) {
            onNewMessageCountIncrease(messages.size - previousMessageCount)
        }

        onPreviousMessageCountChange(messages.size)
    }

    LaunchedEffect(isNearBottom) {
        if (isNearBottom) {
            onNewMessageCountChange(0)
        }
    }

    LaunchedEffect(isNearBottom, messages.size, hasUserScrolledAfterOpen) {
        if (isNearBottom && hasUserScrolledAfterOpen) {
            onShouldShowUnreadDividerChange(false)
            onMessagesVisible()
        }
    }

    LaunchedEffect(isUserDragging) {
        if (isUserDragging) {
            onHasUserScrolledAfterOpenChange(true)
        }
    }

    LaunchedEffect(hasInitialScrollDone, shouldShowUnreadDivider, isNearBottom) {
        if (hasInitialScrollDone && shouldShowUnreadDivider && isNearBottom) {
            delay(1000.milliseconds)
            onShouldShowUnreadDividerChange(false)
            onMessagesVisible()
        }
    }
}
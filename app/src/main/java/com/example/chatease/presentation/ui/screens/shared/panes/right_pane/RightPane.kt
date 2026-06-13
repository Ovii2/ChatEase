package com.example.chatease.presentation.ui.screens.shared.panes.right_pane

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationStarterRow
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessageInputBar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessagesList
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.NewMessagesButton
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.RightPaneTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun RightPane(
    modifier: Modifier = Modifier,
    user: User,
    messages: List<Message>,
    currentUserId: String,
    onBackClick: () -> Unit,
    onSendMessageClick: (String) -> Unit,
    firstUnreadMessageId: String?,
    onMessagesVisible: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onNavigateToChatInfo: () -> Unit,
    isPeekEnabled: Boolean,
    onPeekClick: () -> Unit,
    typingUserIds: List<String>,
    updateTypingStatus: (String) -> Unit,
    isBlockedByOtherUser: Boolean
) {
    val focusManager = LocalFocusManager.current
    var messageText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val isNearBottom by derivedStateOf {
        val firstVisibleItemIndex = listState.firstVisibleItemIndex
        messages.isNotEmpty() && firstVisibleItemIndex <= 1
    }
    val firstIndex = 0

    var hasInitialScrollDone by remember { mutableStateOf(false) }
    var newMessageCount by rememberSaveable { mutableIntStateOf(0) }
    var previousMessageCount by rememberSaveable { mutableIntStateOf(messages.size) }
    val showNewMessagesButton = newMessageCount > 0

    val scope = rememberCoroutineScope()

    var hasUserScrolledAfterOpen by remember { mutableStateOf(false) }
    val isUserDragging by listState.interactionSource.collectIsDraggedAsState()

    var shouldShowUnreadDivider by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size, firstUnreadMessageId) {
        if (messages.isNotEmpty() && !hasInitialScrollDone && firstUnreadMessageId != null) {
            val reversedMessages = messages.reversed()

            val unreadIndex = reversedMessages.indexOfFirst { message ->
                message.messageId == firstUnreadMessageId
            }

            if (unreadIndex != -1) {
                shouldShowUnreadDivider = true
                listState.scrollToItem(unreadIndex)
            } else {
                listState.scrollToItem(firstIndex)
            }

            hasInitialScrollDone = true
            newMessageCount = 0
            previousMessageCount = messages.size
        }
    }

    LaunchedEffect(messages.size) {
        val latestMessage = messages.lastOrNull()

        if (
            messages.size > previousMessageCount &&
            !isNearBottom &&
            latestMessage?.senderId != currentUserId
        ) {
            newMessageCount += messages.size - previousMessageCount
        }

        previousMessageCount = messages.size
    }

    LaunchedEffect(isNearBottom) {
        if (isNearBottom) {
            newMessageCount = 0
        }
    }

    LaunchedEffect(
        isNearBottom,
        messages.size,
        hasUserScrolledAfterOpen
    ) {
        if (isNearBottom && hasUserScrolledAfterOpen) {
            shouldShowUnreadDivider = false
            onMessagesVisible()
        }
    }

    LaunchedEffect(isUserDragging) {
        if (isUserDragging) {
            hasUserScrolledAfterOpen = true
        }
    }


    val firstUserName = user.fullName.substringBefore(" ")
    val secondUsername = user.fullName.substringBefore(" ")

    val typingText = when (typingUserIds.size) {
        1 -> stringResource(R.string.one_is_typing, firstUserName)
        2 -> stringResource(R.string.two_are_typing, firstUserName, secondUsername)
        else -> stringResource(R.string.many_are_typing, typingUserIds.size)
    }


    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .systemBarsPadding()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RightPaneTopBar(
                user = user,
                onBackClick = onBackClick,
                onNavigateToChatInfo = onNavigateToChatInfo,
            )
            if (showNewMessagesButton) {
                NewMessagesButton(
                    onClick = {
                        newMessageCount = 0

                        scope.launch {
                            listState.animateScrollToItem(firstIndex)
                        }
                    },
                    newMessages = newMessageCount
                )
            }
            MessagesList(
                modifier = Modifier.weight(1f),
                messages = messages,
                currentUserId = currentUserId,
                user = user,
                listState = listState,
                firstUnreadMessageId = if (shouldShowUnreadDivider) firstUnreadMessageId else null,
                onReactionClick = { messageId, reaction ->
                    onReactionClick(messageId, reaction)
                },
            )
            if (typingUserIds.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = typingText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            if (messages.isEmpty()) {
                ConversationStarterRow(
                    onStarterClick = { messageText = it }
                )
            }
            MessageInputBar(
                onMicrophoneClick = {},
                onSendMessageClick = {
                    onSendMessageClick(it)
                    messageText = ""

                    scope.launch {
                        listState.animateScrollToItem(firstIndex)
                    }
                },
                messageText = messageText,
                onMessageTextChange = {
                    messageText = it
                    updateTypingStatus(it)
                },
                isPeekEnabled = isPeekEnabled,
                onPeekClick = onPeekClick,
                onInputFocused = {
                    shouldShowUnreadDivider = false
                    onMessagesVisible()
                },
                isBlockedByOtherUser = isBlockedByOtherUser,
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RightPanePreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val messages = listOf(
        Message(
            messageId = "1",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Hey! Are we still for lunch?",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_2")
        ),
        Message(
            messageId = "2",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = LoremIpsum(30).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "3",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Yes, definitely! GG 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "4",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "5",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "6",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        )
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                RightPane(
                    user = user,
                    messages = messages,
                    currentUserId = "user_2",
                    onBackClick = {},
                    onSendMessageClick = {},
                    firstUnreadMessageId = "1",
                    onMessagesVisible = {},
                    onReactionClick = { _, _ -> },
                    onNavigateToChatInfo = {},
                    isPeekEnabled = false,
                    onPeekClick = {},
                    typingUserIds = listOf("user_1", "user_2"),
                    updateTypingStatus = {},
                    isBlockedByOtherUser = false,
                )
            }
        }
    }
}
package com.example.chatease.presentation.ui.screens.shared.panes.right_pane

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
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
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupChatTopBar
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupMessageList
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationStarterRow
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.DirectChatTopBar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessageInputBar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessagesList
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.NewMessagesButton
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun RightPane(
    modifier: Modifier = Modifier,
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
    isBlockedByOtherUser: Boolean,
    onStartAudioCall: (String) -> Unit,
    chatPaneUiState: ChatPaneUiState
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
    var initialUnreadMessageId by rememberSaveable { mutableStateOf<String?>(null) }

    val typingUsers = when (chatPaneUiState) {
        is ChatPaneUiState.DirectChat -> {
            listOf(chatPaneUiState.user).filter { user ->
                user.uid in typingUserIds
            }
        }

        is ChatPaneUiState.GroupChat -> {
            chatPaneUiState.members.filter { user ->
                user.uid in typingUserIds
            }
        }
    }

    val typingNames = typingUsers.map { user ->
        user.fullName.substringBefore(" ")
    }

    val typingText = when (typingUserIds.size) {
        1 -> stringResource(R.string.one_is_typing, typingNames[0])
        2 -> stringResource(R.string.two_are_typing, typingNames[0], typingNames[1])
        else -> stringResource(R.string.many_are_typing, typingUserIds.size)
    }

    RightPaneEffects(
        messages = messages,
        firstUnreadMessageId = firstUnreadMessageId,
        currentUserId = currentUserId,
        listState = listState,
        isNearBottom = isNearBottom,
        isUserDragging = isUserDragging,
        hasInitialScrollDone = hasInitialScrollDone,
        previousMessageCount = previousMessageCount,
        shouldShowUnreadDivider = shouldShowUnreadDivider,
        hasUserScrolledAfterOpen = hasUserScrolledAfterOpen,
        onInitialUnreadMessageIdChange = { initialUnreadMessageId = it },
        onHasInitialScrollDoneChange = { hasInitialScrollDone = it },
        onNewMessageCountChange = { newMessageCount = it },
        onNewMessageCountIncrease = { newMessageCount += it },
        onPreviousMessageCountChange = { previousMessageCount = it },
        onHasUserScrolledAfterOpenChange = { hasUserScrolledAfterOpen = it },
        onShouldShowUnreadDividerChange = { shouldShowUnreadDivider = it },
        onMessagesVisible = onMessagesVisible
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .systemBarsPadding()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (chatPaneUiState) {
            is ChatPaneUiState.DirectChat -> {
                DirectChatTopBar(
                    user = chatPaneUiState.user,
                    onBackClick = onBackClick,
                    onNavigateToChatInfo = onNavigateToChatInfo,
                    isBlockedByOtherUser = isBlockedByOtherUser,
                    onStartAudioCall = onStartAudioCall,
                )

                MessagesList(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    messages = messages,
                    currentUserId = currentUserId,
                    user = chatPaneUiState.user,
                    listState = listState,
                    firstUnreadMessageId = if (shouldShowUnreadDivider) initialUnreadMessageId else null,
                    onReactionClick = { messageId, reaction ->
                        onReactionClick(messageId, reaction)
                    },
                    isBlockedByOtherUser = isBlockedByOtherUser,
                )
            }

            is ChatPaneUiState.GroupChat -> {
                GroupChatTopBar(
                    onBackClick = onBackClick,
                    members = chatPaneUiState.members.size,
                    group = chatPaneUiState.group
                )

                GroupMessageList(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    messages = messages,
                    currentUserId = currentUserId,
                    listState = listState,
                    groupMembers = chatPaneUiState.members,
                    firstUnreadMessageId = firstUnreadMessageId
                )
            }
        }
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

        if (typingUserIds.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
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
            modifier = Modifier,
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


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
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
    val senders = listOf("user_1", "user_2")
    val messages = List(5) {
        Message(
            messageId = it.toString(),
            conversationId = "conversation_1",
            senderId = senders.random(),
            text = LoremIpsum((10..50).random()).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = emptyList(),
            reactions = emptyMap(),
            messageType = MessageType.TEXT
        )
    }

    val directUiState = ChatPaneUiState.DirectChat(
        user = user
    )

    val groupUiState = ChatPaneUiState.GroupChat(
        group = Group(
            conversationId = "1",
            ownerId = "1",
            name = "Test Group",
            imageUrl = null
        ),
        members = List(5) { user }
    )

    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                RightPane(
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
                    onStartAudioCall = {},
                    chatPaneUiState = groupUiState,
                )
            }
        }
    }
}
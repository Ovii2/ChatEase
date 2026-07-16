package com.example.chatease.presentation.ui.screens.group_chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationStarterRow
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessageInputBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun GroupChatScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    focusManager: FocusManager,
    user: User,
    messages: List<Message>,
    currentUserId: String,
    listState: LazyListState,
    groupMembers: List<User>,
    firstUnreadMessageId: String,
    typingUserIds: List<String>,
    typingText: String,
    scope: CoroutineScope,
    firstIndex: Int,
    isPeekEnabled: Boolean,
    isBlockedByOtherUser: Boolean,
    onSendMessageClick: (String) -> Unit,
    updateTypingStatus: (String) -> Unit,
    onMessagesVisible: () -> Unit
) {
    var messageText by rememberSaveable { mutableStateOf("") }
    var shouldShowUnreadDivider by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GroupMessageList(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
            user = user,
            messages = messages,
            currentUserId = currentUserId,
            listState = listState,
            groupMembers = groupMembers,
            firstUnreadMessageId = firstUnreadMessageId
        )
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
        } else {
            Unit
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
            onPeekClick = {},
            onInputFocused = {
                shouldShowUnreadDivider = false
                onMessagesVisible()
            },
            isBlockedByOtherUser = isBlockedByOtherUser,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatScreenContentPreview() {
    val user = User(
        uid = "user_1",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    val messages = List(5) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = "user_1",
            text = "Test message",
            timeStamp = System.currentTimeMillis(),
            seenBy = emptyList(),
            reactions = emptyMap(),
            messageType = MessageType.TEXT
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatScreenContent(
                    paddingValues = PaddingValues(),
                    focusManager = LocalFocusManager.current,
                    user = user,
                    messages = messages,
                    currentUserId = "1",
                    listState = rememberLazyListState(),
                    groupMembers = emptyList(),
                    firstUnreadMessageId = "1",
                    typingUserIds = emptyList(),
                    typingText = "",
                    scope = rememberCoroutineScope(),
                    firstIndex = 0,
                    isPeekEnabled = false,
                    isBlockedByOtherUser = false,
                    onSendMessageClick = {},
                    updateTypingStatus = {},
                    onMessagesVisible = {}
                )
            }
        }
    }
}

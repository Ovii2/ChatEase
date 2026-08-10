package com.example.chatease.presentation.ui.screens.shared.panes.right_pane

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
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
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationStarterRow
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.DirectChatTopBar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessageInputBar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessagesActionPanel
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessagesList
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.NewMessagesButton
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ReplyMessagePanel
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
    onSendMessageClick: (String, Message?) -> Unit,
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
    chatPaneUiState: ChatPaneUiState,
    onNavigateToGroupChatInfo: (String) -> Unit,
    onShowUsersReactionsClick: (String) -> Unit,
    onSendFile: (Uri) -> Unit,
    onFileClick: (Message) -> Unit,
    uploadingFileId: String?,
    fileUploadProgress: Float?,
    pendingFileMessage: Message?
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
    var selectedMessage by remember { mutableStateOf<Message?>(null) }
    var selectedReactionMessageId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedReplyMessage by rememberSaveable { mutableStateOf<Message?>(null) }
    val clipboard = LocalClipboard.current

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

    val typingText = when (typingUsers.size) {
        1 -> stringResource(R.string.one_is_typing, typingNames[0])
        2 -> stringResource(R.string.two_are_typing, typingNames[0], typingNames[1])
        else -> stringResource(R.string.many_are_typing, typingUsers.size)
    }

    val isUserGroupMember =
        (chatPaneUiState as? ChatPaneUiState.GroupChat)?.group?.userIds?.contains(currentUserId)
            ?: true

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            onSendFile(uri)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->


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

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .systemBarsPadding()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                selectedReactionMessageId = null
                selectedMessage = null
                selectedReplyMessage = null
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
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
                }

                is ChatPaneUiState.GroupChat -> {
                    GroupChatTopBar(
                        onBackClick = onBackClick,
                        members = chatPaneUiState.group.userIds.size,
                        group = chatPaneUiState.group,
                        onNavigateToGroupChatInfo = onNavigateToGroupChatInfo,
                    )
                }
            }

            MessagesList(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .weight(1f),
                messages = messages,
                currentUserId = currentUserId,
                listState = listState,
                firstUnreadMessageId = if (shouldShowUnreadDivider) initialUnreadMessageId else null,
                onReactionClick = { messageId, reaction ->
                    onReactionClick(messageId, reaction)
                    selectedMessage = null
                    selectedReactionMessageId = null
                    selectedReplyMessage = null
                },
                isBlockedByOtherUser = isBlockedByOtherUser,
                isUserMemberOfGroup = isUserGroupMember,
                chatPaneUiState = chatPaneUiState,
                onShowUsersReactionsClick = onShowUsersReactionsClick,
                onLongClick = {
                    selectedMessage = it
                },
                selectedReactionMessageId = selectedReactionMessageId,
                onSelectedReactionMessageIdChange = { selectedReactionMessageId = it },
                onDismissMessageActions = {
                    selectedMessage = null
                    selectedReactionMessageId = null
                    selectedReplyMessage = null
                },
                onFileClick = onFileClick,
                uploadingFileId = uploadingFileId,
                fileUploadProgress = fileUploadProgress,
                pendingFileMessage = pendingFileMessage,
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

            if (typingUsers.isNotEmpty()) {
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

            selectedReplyMessage?.let { message ->
                val replyUserName = when (chatPaneUiState) {
                    is ChatPaneUiState.DirectChat -> {
                        chatPaneUiState.user.fullName
                    }

                    is ChatPaneUiState.GroupChat -> {
                        chatPaneUiState.members
                            .firstOrNull { user ->
                                user.uid == message.senderId
                            }
                            ?.fullName
                            .orEmpty()
                    }
                }
                ReplyMessagePanel(
                    onDismiss = {
                        selectedReplyMessage = null
                    },
                    isCurrentUser = message.senderId == currentUserId,
                    message = message.text,
                    replyUserName = replyUserName
                )
            }

            MessageInputBar(
                modifier = Modifier,
                onMicrophoneClick = {},
                onSendMessageClick = {
                    onSendMessageClick(it, selectedReplyMessage)
                    messageText = ""

                    scope.launch {
                        listState.animateScrollToItem(firstIndex)
                    }
                    selectedReplyMessage = null
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
                isUserGroupMember = isUserGroupMember,
                onAddFileClick = {
                    filePickerLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "text/plain"
                        )
                    )
                },
                onAddImageClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = selectedMessage != null,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }
            ),
            label = "Action panel"
        ) {
            selectedMessage?.let { message ->
                MessagesActionPanel(
                    modifier = Modifier,
                    isSenderCurrentUser = message.senderId == currentUserId,
                    onReplyClick = {
                        selectedReplyMessage = message
                        selectedMessage = null
                        selectedReactionMessageId = null
                    },
                    onCopyClick = {
                        scope.launch {
                            clipboard.setClipEntry(
                                ClipEntry(ClipData.newPlainText("message", message.text))
                            )
                        }
                        selectedMessage = null
                        selectedReactionMessageId = null
                    }
                )
            }
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RightPanePreview() {
    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    val senders = listOf("1", "2")
    val messages = List(5) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = senders.random(),
            text = LoremIpsum((10..20).random()).values.first(),
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
            userIds = listOf("1", "2"),
            visibleToUserIds = emptyList(),
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
                    currentUserId = "1",
                    onBackClick = {},
                    onSendMessageClick = { _, _ -> },
                    firstUnreadMessageId = "1",
                    onMessagesVisible = {},
                    onReactionClick = { _, _ -> },
                    onNavigateToChatInfo = {},
                    isPeekEnabled = false,
                    onPeekClick = {},
                    typingUserIds = listOf("1", "2"),
                    updateTypingStatus = {},
                    isBlockedByOtherUser = false,
                    onStartAudioCall = {},
                    chatPaneUiState = groupUiState,
                    onNavigateToGroupChatInfo = {},
                    onShowUsersReactionsClick = {},
                    onSendFile = {},
                    onFileClick = {},
                    uploadingFileId = "",
                    fileUploadProgress = 1f,
                    pendingFileMessage = null,
                )
            }
        }
    }
}

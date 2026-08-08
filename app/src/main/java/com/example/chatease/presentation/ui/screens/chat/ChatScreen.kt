package com.example.chatease.presentation.ui.screens.chat

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.viewmodel.CallViewModel
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel
import com.example.chatease.utils.openFile

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = hiltViewModel(),
    callViewModel: CallViewModel = hiltViewModel(),
    conversationId: String,
    onBackClick: () -> Unit,
    onNavigateToChatInfo: (String) -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToAudioCallScreen: (String) -> Unit
) {
    val user by chatViewModel.user.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    val isConversationDeleted by chatViewModel.isConversationDeleted.collectAsState()
    val typingUserIds by chatViewModel.typingUserIds.collectAsState()
    val isBlockedByOtherUser by chatViewModel.isBlockedByOtherUser.collectAsState()
    val currentUserId = chatViewModel.currentUserId
    val context = LocalContext.current
    val openingFileMessageId by chatViewModel.openingFileMessageId.collectAsState()

    LaunchedEffect(conversationId) {
        chatViewModel.loadConversation(conversationId)
    }

    LaunchedEffect(isConversationDeleted) {
        if (isConversationDeleted) {
            onNavigateToHomeScreen()
        }
    }

    BackHandler {
        chatViewModel.markMessagesAsSeen(conversationId)
        chatViewModel.updateTypingStatus(conversationId, false)
        chatViewModel.deleteConversationIfEmpty(conversationId)
        onBackClick()
    }

    RightPane(
        messages = messages,
        currentUserId = chatViewModel.currentUserId,
        onBackClick = {
            chatViewModel.markMessagesAsSeen(conversationId)
            chatViewModel.deleteConversationIfEmpty(conversationId)
            chatViewModel.updateTypingStatus(conversationId, false)
            onBackClick()
        },
        onSendMessageClick = { text, repliedMessage ->
            chatViewModel.sendMessage(
                conversationId = conversationId,
                text = text,
                repliedMessage = repliedMessage
            )
            chatViewModel.updateTypingStatus(
                conversationId = conversationId,
                isTyping = false
            )
        },
        firstUnreadMessageId = chatViewModel.firstUnreadMessageId,
        onMessagesVisible = { chatViewModel.markMessagesAsSeen(conversationId) },
        onReactionClick = { messageId, reaction ->
            chatViewModel.addReactionToMessage(
                conversationId = conversationId,
                messageId = messageId,
                reaction = reaction
            )
        },
        onNavigateToChatInfo = { onNavigateToChatInfo(conversationId) },
        isPeekEnabled = isPeekEnabled,
        onPeekClick = { isPeekEnabled = !isPeekEnabled },
        typingUserIds = typingUserIds,
        updateTypingStatus = {
            chatViewModel.updateTypingStatus(
                conversationId = conversationId,
                isTyping = it.isNotBlank(),
            )
        },
        isBlockedByOtherUser = isBlockedByOtherUser,
        onStartAudioCall = { receiverId ->
            callViewModel.createCall(
                receiverId = receiverId,
                callType = CallType.AUDIO,
                onCallCreated = { callId ->
                    onNavigateToAudioCallScreen(callId)
                },
                conversationId = conversationId
            )
        },
        chatPaneUiState = ChatPaneUiState.DirectChat(
            user = user
        ),
        onNavigateToGroupChatInfo = {},
        onShowUsersReactionsClick = {},
        onSendFile = { uri ->
            chatViewModel.sendFile(
                conversationId = conversationId,
                fileUri = uri,
                currentUserId = currentUserId
            )
        },
        onFileClick = { message ->
            val fileAttachment = message.fileAttachment ?: return@RightPane

            chatViewModel.openFile(
                messageId = message.messageId,
                fileUrl = fileAttachment.url,
                fileName = fileAttachment.name,
                onFileReady = { uri ->
                    openFile(
                        context = context,
                        uri = uri,
                        mimeType = fileAttachment.mimeType
                    )
                }
            )
        },
        openingFileMessageId = openingFileMessageId,
    )
}
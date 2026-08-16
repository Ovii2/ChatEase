package com.example.chatease.presentation.ui.screens.group_chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.enums.FileDownloadState
import com.example.chatease.presentation.ui.screens.shared.chat.ReactionsDetailsBottomSheet
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ImageViewerDialog
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.state.GroupChatUiState
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel
import com.example.chatease.presentation.ui.viewmodel.GroupChatViewModel
import com.example.chatease.utils.openFile

@Composable
fun GroupChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    conversationId: String,
    groupChatViewModel: GroupChatViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    onNavigateToGroupChatInfo: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    val isBlockedByOtherUser = false
    val uiState by groupChatViewModel.uiState.collectAsState()
    val currentUserId = chatViewModel.currentUserId
    val context = LocalContext.current
    val isUserGroupMember =
        (uiState as? GroupChatUiState.Success)
            ?.group
            ?.userIds
            ?.contains(currentUserId)
            ?: false
    val fileDownloadUiState by chatViewModel.fileDownloadUiState.collectAsState()
    val failedDownloadFileMessage = stringResource(R.string.failed_download_file)
    val successDownloadFileMessage = stringResource(R.string.success_download_file)
    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(conversationId) {
        groupChatViewModel.loadGroupConversation(conversationId)
        chatViewModel.loadConversation(conversationId)
    }

    LaunchedEffect(fileDownloadUiState) {
        when (fileDownloadUiState.state) {
            FileDownloadState.SUCCESS -> {
                snackbarHostState.showSnackbar(
                    message = successDownloadFileMessage,
                    actionLabel = "",
                    duration = SnackbarDuration.Short
                )
            }

            FileDownloadState.FAILED -> {
                snackbarHostState.showSnackbar(
                    message = failedDownloadFileMessage,
                    actionLabel = "",
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }
    }

    BackHandler {
        if (isUserGroupMember) {
            chatViewModel.markMessagesAsSeen(conversationId)
        }

        chatViewModel.updateTypingStatus(conversationId, false)
        chatViewModel.deleteConversationIfEmpty(conversationId)
        onBackClick()
    }

    val firstUnreadMessageId = chatViewModel.firstUnreadMessageId ?: ""
    val typingUserIds by chatViewModel.typingUserIds.collectAsState()
    var selectedReactionsMessageId by rememberSaveable { mutableStateOf<String?>(null) }

    val fileUploadProgress by chatViewModel.fileUploadProgress.collectAsState()
    val uploadingFileId by chatViewModel.uploadingFileId.collectAsState()
    val pendingFileMessage by chatViewModel.pendingFileMessage.collectAsState()

    when (val state = uiState) {
        is GroupChatUiState.Success -> {
            val selectedMessage = state.messages.firstOrNull { message ->
                message.messageId == selectedReactionsMessageId
            }

            Scaffold(
                modifier = modifier.padding(vertical = 8.dp),
            ) { paddingValues ->
                RightPane(
                    messages = state.messages,
                    currentUserId = currentUserId,
                    onBackClick = {
                        if (isUserGroupMember) {
                            chatViewModel.markMessagesAsSeen(conversationId)
                        }

                        chatViewModel.updateTypingStatus(conversationId, false)
                        chatViewModel.deleteConversationIfEmpty(conversationId)
                        onBackClick()
                    },
                    onSendMessageClick = { text, repliedMessage ->
                        chatViewModel.sendMessage(
                            conversationId = conversationId,
                            text = text,
                            repliedMessage = repliedMessage
                        )
                    },
                    firstUnreadMessageId = firstUnreadMessageId,
                    onMessagesVisible = { chatViewModel.markMessagesAsSeen(conversationId) },
                    onReactionClick = { messageId, reaction ->
                        chatViewModel.addReactionToMessage(
                            conversationId = conversationId,
                            messageId = messageId,
                            reaction = reaction
                        )
                    },
                    onNavigateToChatInfo = {},
                    isPeekEnabled = isPeekEnabled,
                    onPeekClick = { isPeekEnabled = !isPeekEnabled },
                    typingUserIds = typingUserIds,
                    updateTypingStatus = { text ->
                        chatViewModel.updateTypingStatus(
                            conversationId = conversationId,
                            isTyping = text.isNotBlank()
                        )
                    },
                    isBlockedByOtherUser = isBlockedByOtherUser,
                    onStartAudioCall = {},
                    chatPaneUiState = ChatPaneUiState.GroupChat(
                        group = state.group,
                        members = state.members
                    ),
                    onNavigateToGroupChatInfo = onNavigateToGroupChatInfo,
                    onShowUsersReactionsClick = { messageId ->
                        selectedReactionsMessageId = messageId
                    },
                    onSendFile = { uri ->
                        chatViewModel.sendFile(
                            conversationId = conversationId,
                            fileUri = uri,
                            currentUserId = currentUserId
                        )
                    },
                    onFileClick = { message ->
                        val fileAttachment = message.fileAttachments.firstOrNull() ?: return@RightPane

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
                    uploadingFileId = uploadingFileId,
                    fileUploadProgress = fileUploadProgress,
                    pendingFileMessage = pendingFileMessage,
                    onDownloadClick = {},
                    fileDownloadUiState = fileDownloadUiState,
                    snackbarHostState = snackbarHostState,
                    onSendImage = { uri ->
                        chatViewModel.sendImage(
                            conversationId = conversationId,
                            imageUri = uri,
                            currentUserId = currentUserId
                        )
                    },
                    onImageClick = { message ->
                        selectedImageUrl = message.fileAttachments.firstOrNull()?.url
                    },
                )
                selectedMessage?.let { message ->
                    ReactionsDetailsBottomSheet(
                        onDismissRequest = { selectedReactionsMessageId = null },
                        users = state.members.filter { user ->
                            user.uid in message.reactions.keys
                        },
                        reactions = message.reactions
                    )
                }
                selectedImageUrl?.let { url ->
                    ImageViewerDialog(
                        onDismiss = { selectedImageUrl = null },
                        imageUrl = url,
                        onDownloadClick = {}
                    )
                }
            }
        }

        is GroupChatUiState.Error -> {}
        GroupChatUiState.Loading -> {
            CommonCircularLoader()
        }
    }
}

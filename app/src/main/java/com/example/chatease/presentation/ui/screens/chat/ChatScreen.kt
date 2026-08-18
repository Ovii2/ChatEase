package com.example.chatease.presentation.ui.screens.chat

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.model.enums.FileDownloadState
import com.example.chatease.presentation.ui.screens.shared.chat.ReactionsDetailsBottomSheet
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ImageViewerDialog
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
    onNavigateToAudioCallScreen: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val user by chatViewModel.user.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    val isConversationDeleted by chatViewModel.isConversationDeleted.collectAsState()
    val typingUserIds by chatViewModel.typingUserIds.collectAsState()
    val isBlockedByOtherUser by chatViewModel.isBlockedByOtherUser.collectAsState()
    val currentUserId = chatViewModel.currentUserId
    val context = LocalContext.current
    val fileUploadProgress by chatViewModel.fileUploadProgress.collectAsState()
    val uploadingFileId by chatViewModel.uploadingFileId.collectAsState()
    val pendingFileMessage by chatViewModel.pendingFileMessage.collectAsState()
    var pendingDownloadMessage by rememberSaveable { mutableStateOf<Message?>(null) }
    val fileDownloadUiState by chatViewModel.fileDownloadUiState.collectAsState()

    val failedDownloadFileMessage = stringResource(R.string.failed_download_file)
    val successDownloadFileMessage = stringResource(R.string.success_download_file)

    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    val pendingImageMessage by chatViewModel.pendingImageMessage.collectAsState()
    var selectedReactionsMessageId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedReactionsMessage = messages.firstOrNull { message ->
        message.messageId == selectedReactionsMessageId
    }

    val currentUser by chatViewModel.currentUser.collectAsState()


    LaunchedEffect(conversationId) {
        chatViewModel.loadConversation(conversationId)
        chatViewModel.preLoadMediaItems(conversationId)
    }

    LaunchedEffect(isConversationDeleted) {
        if (isConversationDeleted) {
            onNavigateToHomeScreen()
        }
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
        chatViewModel.markMessagesAsSeen(conversationId)
        chatViewModel.updateTypingStatus(conversationId, false)
        chatViewModel.deleteConversationIfEmpty(conversationId)
        onBackClick()
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val message =
                pendingDownloadMessage ?: return@rememberLauncherForActivityResult

            val fileAttachment =
                pendingDownloadMessage?.fileAttachments?.firstOrNull()
                    ?: return@rememberLauncherForActivityResult

            chatViewModel.downloadFile(
                messageId = message.messageId,
                fileUrl = fileAttachment.url,
                fileName = fileAttachment.name,
                mimeType = fileAttachment.mimeType
            )
            pendingDownloadMessage = null
        }
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
            val fileAttachment = message.fileAttachments?.firstOrNull() ?: return@RightPane

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
        pendingImageMessage = pendingImageMessage,
        onDownloadClick = { message ->
            val fileAttachment = message.fileAttachments.firstOrNull() ?: return@RightPane

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                pendingDownloadMessage = message
                storagePermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } else {
                chatViewModel.downloadFile(
                    messageId = message.messageId,
                    fileUrl = fileAttachment.url,
                    fileName = fileAttachment.name,
                    mimeType = fileAttachment.mimeType
                )
            }
        },
        fileDownloadUiState = fileDownloadUiState,
        snackbarHostState = snackbarHostState,
        onSendImages = { uris ->
            chatViewModel.sendImages(
                conversationId = conversationId,
                imageUris = uris,
                currentUserId = currentUserId
            )
        },
        onImageClick = { fileAttachment ->
            selectedImageUrl = fileAttachment.url
        },
        onRemoveReactionClick = { messageId, _ ->
            chatViewModel.removeReactionFromMessage(
                conversationId = conversationId,
                messageId = messageId
            )
        },
    )
    selectedReactionsMessage?.let { message ->
        ReactionsDetailsBottomSheet(
            onDismissRequest = { selectedReactionsMessageId = null },
            users = listOf(currentUser, user).filter { user ->
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
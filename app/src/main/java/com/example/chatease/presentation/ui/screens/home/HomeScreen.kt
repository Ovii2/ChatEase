package com.example.chatease.presentation.ui.screens.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.AlertDialogType
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.ConversationUiModel
import com.example.chatease.presentation.ui.screens.home.layouts.HomeCompactLayout
import com.example.chatease.presentation.ui.screens.home.layouts.HomeTabletLayout
import com.example.chatease.presentation.ui.screens.shared.chat.ChatNavigationScaffold
import com.example.chatease.presentation.ui.screens.shared.chat.CommonAlertDialog
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationOptionsBottomSheet
import com.example.chatease.presentation.ui.screens.shared.chat.StartChatFab
import com.example.chatease.presentation.ui.screens.shared.error.CommonErrorDisplay
import com.example.chatease.presentation.ui.screens.shared.loading.CommonLinearLoader
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.LeftPane
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ImageViewerDialog
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.state.HomeUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AuthViewModel
import com.example.chatease.presentation.ui.viewmodel.CallViewModel
import com.example.chatease.presentation.ui.viewmodel.ChatInfoViewModel
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel
import com.example.chatease.presentation.ui.viewmodel.GroupChatInfoViewModel
import com.example.chatease.presentation.ui.viewmodel.HomeViewModel
import com.example.chatease.utils.openFile

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToCalls: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onConversationClick: (String, Boolean) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    chatInfoViewModel: ChatInfoViewModel = hiltViewModel(),
    groupChatInfoViewModel: GroupChatInfoViewModel = hiltViewModel(),
    callViewModel: CallViewModel = hiltViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onStartNewChat: () -> Unit,
    currentRoute: String,
    onBackClick: () -> Unit,
    onNavigateToChatInfo: (String) -> Unit,
    onViewContactClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    onNavigateToMediaAndDocsScreen: (String) -> Unit,
    onNavigateToMembershipScreen: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)

    val pendingRequests by contactsViewModel.pendingRequests.collectAsState()
    val unreadMessages = (uiState as? HomeUiState.Success)?.unreadMessages ?: 0

    val messages by chatViewModel.messages.collectAsState()
    var selectedConversationId by rememberSaveable { mutableStateOf<String?>(null) }
    val user by chatViewModel.user.collectAsState()

    val isCompactWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isShortHeight = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    val usePhoneLayout = isCompactWidth || isShortHeight

    val keyboardController = LocalSoftwareKeyboardController.current

    val isConversationCreator by chatViewModel.isConversationCreator.collectAsState()
    val isBlockedByOtherUser by chatViewModel.isBlockedByOtherUser.collectAsState()

    val missedCalls by callViewModel.missedCallsCount.collectAsState()

    var showConversationOptionsBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showLeaveGroupDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConversationDialog by rememberSaveable { mutableStateOf(false) }
    var selectedConversationIsGroup by rememberSaveable { mutableStateOf(false) }
    val isOwner by groupChatInfoViewModel.isOwner.collectAsState()
    val isGroupMember by groupChatInfoViewModel.isGroupMember.collectAsState()

    val searchValue by homeViewModel.searchValue.collectAsState()
    val currentUserId = chatViewModel.currentUserId

    val context = LocalContext.current

    val fileUploadProgress by chatViewModel.fileUploadProgress.collectAsState()
    val uploadingFileId by chatViewModel.uploadingFileId.collectAsState()
    val pendingFileMessage by chatViewModel.pendingFileMessage.collectAsState()
    val pendingImageMessage by chatViewModel.pendingImageMessage.collectAsState()

    val fileDownloadUiState by chatViewModel.fileDownloadUiState.collectAsState()
    val mediaItems by chatInfoViewModel.mediaItems.collectAsState()

    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }

    val typingTexts by chatViewModel.typingTexts.collectAsState()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }

    HomeScreenEffects(
        selectedConversationId = selectedConversationId,
        onLoadConversation = chatViewModel::loadConversation,
        messages = messages,
        windowSizeClass = windowSizeClass,
        focusManager = focusManager,
        keyboardController = keyboardController,
        onObserveMissedCallsCount = callViewModel::observeMissedCallsCount
    )

    ChatNavigationScaffold(
        windowSizeClass = windowSizeClass,
        currentRoute = currentRoute,
        unreadMessages = unreadMessages,
        pendingRequests = pendingRequests.size,
        missedCalls = missedCalls,
        onNavigateToHome = onNavigateToHome,
        onNavigateToContacts = onNavigateToContacts,
        onNavigateToCalls = onNavigateToCalls,
        onNavigateToProfile = onNavigateToProfile
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                },
            floatingActionButton = {
                StartChatFab(
                    modifier = Modifier.padding(8.dp),
                    onStartNewChat = onStartNewChat
                )
            }) { paddingValues ->
            when (val state = uiState) {
                is HomeUiState.Error -> {
                    CommonErrorDisplay(
                        showActionButton = true,
                        onRetryClick = homeViewModel::loadHomeData
                    )
                }

                HomeUiState.Loading -> {
                    CommonLinearLoader()
                }

                is HomeUiState.Success -> {
                    when {
                        usePhoneLayout -> {
                            HomeCompactLayout(
                                paddingValues = paddingValues,
                                state = state,
                                selectedCategory = selectedCategory,
                                onSelectCategory = homeViewModel::selectCategory,
                                onConversationClick = { conversationId, isGroup ->
                                    onConversationClick(
                                        conversationId,
                                        isGroup
                                    )
                                    homeViewModel.clearSearch()
                                },
                                focusManager = focusManager,
                                onLogoutClick = {
                                    authViewModel.logout()
                                    onNavigateToLoginScreen()
                                },
                                onNavigateToProfile = onNavigateToProfile,
                                onLongClick = { conversationId, isGroup ->
                                    selectedConversationId = conversationId
                                    selectedConversationIsGroup = isGroup
                                    if (isGroup) {
                                        groupChatInfoViewModel.checkIfUserIsGroupOwner(
                                            conversationId
                                        )
                                        groupChatInfoViewModel.checkIfUserIsGroupMember(
                                            conversationId
                                        )
                                    }
                                    showConversationOptionsBottomSheet = true
                                },
                                searchValue = searchValue,
                                onSearchValueChange = homeViewModel::onSearchValueChange,
                                currentUserId = currentUserId,
                                onNavigateToMembershipScreen = onNavigateToMembershipScreen
                            )
                        }

                        else -> {
                            AutoSelectFirstConversation(
                                conversations = state.conversations,
                                selectedConversationId = selectedConversationId,
                                onConversationSelected = { selectedConversationId = it },
                                isCompact = false
                            )
                            HomeTabletLayout(
                                paddingValues = paddingValues,
                                state = state,
                                selectedCategory = selectedCategory,
                                focusManager = focusManager,
                                messages = messages,
                                isConversationCreator = isConversationCreator,
                                onSelectCategory = homeViewModel::selectCategory,
                                onConversationClick = { conversationId, _ ->
                                    selectedConversationId = conversationId
                                    homeViewModel.clearSearch()
                                },
                                onLogoutClick = {
                                    authViewModel.logout()
                                    onNavigateToLoginScreen()
                                },
                                onNavigateToProfile = onNavigateToProfile,
                                currentUserId = chatViewModel.currentUserId,
                                onBackClick = onBackClick,
                                onSendMessageClick = { text, repliedMessage ->
                                    selectedConversationId?.let { id ->
                                        chatViewModel.sendMessage(
                                            conversationId = id,
                                            text = text,
                                            repliedMessage = repliedMessage
                                        )
                                    }
                                },
                                firstUnreadMessageId = chatViewModel.firstUnreadMessageId,
                                onMessagesVisible = {
                                    selectedConversationId?.let { id ->
                                        chatViewModel.markMessagesAsSeen(id)
                                    }
                                },
                                onReactionClick = { messageId, reaction ->
                                    selectedConversationId?.let { id ->
                                        chatViewModel.addReactionToMessage(
                                            conversationId = id,
                                            messageId = messageId,
                                            reaction = reaction
                                        )
                                    }
                                },
                                onNavigateToChatInfo = {
                                    selectedConversationId?.let { id ->
                                        onNavigateToChatInfo(id)
                                    }
                                },
                                isBlockedByOtherUser = isBlockedByOtherUser,
                                isBlockedByMe = false,
                                chatPaneUiState = ChatPaneUiState.DirectChat(
                                    user = user
                                ),
                                onNavigateToGroupChatInfo = {},
                                onLongClick = { conversationId, isGroup ->
                                    selectedConversationId = conversationId
                                    selectedConversationIsGroup = isGroup
                                    if (isGroup) {
                                        groupChatInfoViewModel.checkIfUserIsGroupOwner(
                                            conversationId
                                        )
                                        groupChatInfoViewModel.checkIfUserIsGroupMember(
                                            conversationId
                                        )
                                    }
                                    showConversationOptionsBottomSheet = true
                                },
                                onViewContactClick = onViewContactClick,
                                searchValue = searchValue,
                                onSearchValueChange = homeViewModel::onSearchValueChange,
                                onSendFile = { uri ->
                                    selectedConversationId?.let { conversationId ->
                                        chatViewModel.sendFile(
                                            conversationId = conversationId,
                                            fileUri = uri,
                                            currentUserId = currentUserId
                                        )
                                    }
                                },
                                onFileClick = { message ->
                                    val fileAttachment =
                                        message.fileAttachments.firstOrNull()
                                            ?: return@HomeTabletLayout

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
                                    val fileAttachment =
                                        message.fileAttachments.firstOrNull()
                                            ?: return@HomeTabletLayout

                                    chatViewModel.downloadFile(
                                        messageId = message.messageId,
                                        fileUrl = fileAttachment.url,
                                        fileName = fileAttachment.name,
                                        mimeType = fileAttachment.mimeType
                                    )
                                },
                                fileDownloadUiState = fileDownloadUiState,
                                snackbarHostState = snackbarHostState,
                                mediaItems = mediaItems,
                                onNavigateToMediaAndDocsScreen = {
                                    selectedConversationId?.let { conversationId ->
                                        onNavigateToMediaAndDocsScreen(conversationId)
                                    }
                                },
                                onSendImages = { uris ->
                                    selectedConversationId?.let { conversationId ->
                                        chatViewModel.sendImages(
                                            conversationId = conversationId,
                                            imageUris = uris,
                                            currentUserId = currentUserId
                                        )
                                    }
                                },
                                onImageClick = { fileAttachment ->
                                    selectedImageUrl = fileAttachment.url
                                },
                                onRemoveReactionClick = { messageId, _ ->
                                    selectedConversationId?.let { conversationId ->
                                        chatViewModel.removeReactionFromMessage(
                                            conversationId = conversationId,
                                            messageId = messageId
                                        )
                                    }
                                },
                                onNavigateToMembershipScreen = onNavigateToMembershipScreen,
                                onTogglePeek = { isPeekEnabled = !isPeekEnabled },
                                isPeekEnabled = isPeekEnabled,
                                typingTexts = typingTexts,
                            )
                        }
                    }
                    if (showConversationOptionsBottomSheet) {
                        ConversationOptionsBottomSheet(
                            onDismiss = { showConversationOptionsBottomSheet = false },
                            onLeaveGroup = { showLeaveGroupDialog = true },
                            onDeleteConversation = { showDeleteConversationDialog = true },
                            isGroup = selectedConversationIsGroup,
                            isGroupMember = isGroupMember,
                        )
                    }
                    if (showLeaveGroupDialog) {
                        CommonAlertDialog(
                            title = R.string.leave_group,
                            bodyText = R.string.leave_group_text,
                            dismissButtonText = R.string.dismiss_btn,
                            acceptButtonText = R.string.leave,
                            onDismiss = {
                                showLeaveGroupDialog = false
                                showConversationOptionsBottomSheet = false
                            },
                            onAccept = {
                                selectedConversationId?.let {
                                    if (isOwner) {
                                        groupChatInfoViewModel.leaveGroupAsOwner(it)
                                    } else {
                                        groupChatInfoViewModel.leaveGroup(it)
                                    }
                                }
                                showLeaveGroupDialog = false
                                showConversationOptionsBottomSheet = false
                            },
                            alertDialogType = AlertDialogType.CONFIRMATION
                        )
                    }
                    if (showDeleteConversationDialog) {
                        CommonAlertDialog(
                            title = R.string.confirm_conversation_delete_title,
                            bodyText = R.string.confirm_conversation_delete_body,
                            dismissButtonText = R.string.dismiss_btn,
                            acceptButtonText = R.string.delete,
                            onDismiss = {
                                showDeleteConversationDialog = false
                                showConversationOptionsBottomSheet = false
                            },
                            onAccept = {
                                selectedConversationId?.let {
                                    if (selectedConversationIsGroup) {
                                        chatInfoViewModel.deleteGroupConversation(it)
                                    } else {
                                        chatInfoViewModel.deleteConversation(it)
                                    }
                                }
                                showDeleteConversationDialog = false
                                showConversationOptionsBottomSheet = false
                            },
                            alertDialogType = AlertDialogType.CONFIRMATION
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
        }
    }
}

@Composable
private fun AutoSelectFirstConversation(
    conversations: List<ConversationUiModel>,
    selectedConversationId: String?,
    onConversationSelected: (String) -> Unit,
    isCompact: Boolean
) {
    LaunchedEffect(conversations, isCompact) {
        if (!isCompact && selectedConversationId == null) {
            conversations.firstOrNull()?.let { conversation ->
                onConversationSelected(conversation.conversationId)
            }
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun HomeScreenCompactLayoutPreview() {
    val user = User(
        uid = "1",
        fullName = "Test test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.AWAY
    )

    val categories = List(10) {
        Category(
            id = it.toString(),
            name = R.string.friends,
            icon = Icons.Outlined.Group
        )
    }

    val conversation = ConversationUiModel(
        conversationId = "1",
        title = "Test Test",
        imageUrl = null,
        participants = listOf(user),
        lastMessage = "",
        timestamp = System.currentTimeMillis(),
        unreadCount = 0,
        isGroup = false,
        isCurrentUserGroupMember = true,
        lastMessageType = MessageType.TEXT,
        isBlockedByOtherUser = false,
        categoryId = "1",
        lastMessageSenderId = "1",
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            LeftPane(
                modifier = Modifier.padding(paddingValues),
                user = user,
                categories = categories,
                selectedCategory = "all",
                onSelectCategory = {},
                onConversationClick = { _, _ -> },
                onNavigateToMembershipScreen = {},
                conversations = List(3) { conversation },
                focusManager = LocalFocusManager.current,
                onLogoutClick = {},
                onNavigateToProfile = {},
                onLongClick = { _, _ -> },
                searchValue = "",
                onSearchValueChange = {},
                currentUserId = "1",
            )
        }
    }
}

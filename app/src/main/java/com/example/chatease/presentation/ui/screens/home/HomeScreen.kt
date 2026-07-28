package com.example.chatease.presentation.ui.screens.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.ConversationUiModel
import com.example.chatease.presentation.ui.screens.home.layouts.HomeCompactLayout
import com.example.chatease.presentation.ui.screens.home.layouts.HomeTabletLayout
import com.example.chatease.presentation.ui.screens.shared.chat.ChatNavigationScaffold
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationOptionsBottomSheet
import com.example.chatease.presentation.ui.screens.shared.chat.StartChatFab
import com.example.chatease.presentation.ui.screens.shared.error.CommonErrorDisplay
import com.example.chatease.presentation.ui.screens.shared.loading.CommonLinearLoader
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.LeftPane
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.state.HomeUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AuthViewModel
import com.example.chatease.presentation.ui.viewmodel.CallViewModel
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel
import com.example.chatease.presentation.ui.viewmodel.HomeViewModel

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
    callViewModel: CallViewModel = hiltViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onStartNewChat: () -> Unit,
    currentRoute: String,
    onBackClick: () -> Unit,
    onNavigateToChatInfo: (String) -> Unit,
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
            modifier = Modifier.fillMaxSize(),
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
                                onConversationClick = onConversationClick,
                                focusManager = focusManager,
                                onLogoutClick = {
                                    authViewModel.logout()
                                    onNavigateToLoginScreen()
                                },
                                onNavigateToProfile = onNavigateToProfile,
                                onLongClick = {
                                    showConversationOptionsBottomSheet = true
                                },
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
                                },
                                onLogoutClick = {
                                    authViewModel.logout()
                                    onNavigateToLoginScreen()
                                },
                                onNavigateToProfile = onNavigateToProfile,
                                currentUserId = chatViewModel.currentUserId,
                                onBackClick = onBackClick,
                                onSendMessageClick = {
                                    selectedConversationId?.let { id ->
                                        chatViewModel.sendMessage(
                                            id,
                                            it
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
                                onLongClick = {
                                    showConversationOptionsBottomSheet = true
                                },
                            )
                        }
                    }
                    if (showConversationOptionsBottomSheet) {
                        ConversationOptionsBottomSheet(
                            onDismiss = { showConversationOptionsBottomSheet = false }
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

    val categories = listOf(
        Category(id = "all", name = "All"),
        Category(id = "work", name = "Work"),
        Category(id = "friends", name = "Friends")
    )

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
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            LeftPane(
                modifier = Modifier.padding(paddingValues),
                user = user,
                categories = categories,
                selectedCategory = "All",
                onSelectCategory = {},
                onConversationClick = { _, _ -> },
                onClickToSeeAll = {},
                conversations = List(3) { conversation },
                focusManager = LocalFocusManager.current,
                onLogoutClick = {},
                onNavigateToProfile = {},
                onLongClick = {},
            )
        }
    }
}

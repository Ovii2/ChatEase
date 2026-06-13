package com.example.chatease.presentation.ui.screens.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.ConversationUiModel
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.screens.shared.chat.StartChatFab
import com.example.chatease.presentation.ui.screens.shared.chat.chatNavigationSuiteItems
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.ExtraPane
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.LeftPane
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.state.HomeUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AuthViewModel
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel
import com.example.chatease.presentation.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
    onConversationClick: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onStartNewChat: () -> Unit,
    currentRoute: String,
    onBackClick: () -> Unit,
    onNavigateToChatInfo: (String) -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator()

    val uiState by homeViewModel.uiState.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()

    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)

    val pendingRequests by contactsViewModel.pendingRequests.collectAsState()
    val unreadMessages = (uiState as? HomeUiState.Success)?.unreadMessages ?: 0

    val customNavSuiteType = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationSuiteType.NavigationBar
        WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
        WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }

    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val indicatorColor = Color.Transparent
    val navigationContainerColor = MaterialTheme.colorScheme.surfaceContainerLow

    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            indicatorColor = indicatorColor
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            indicatorColor = indicatorColor
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor
        )
    )

    val messages by chatViewModel.messages.collectAsState()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    var selectedConversationId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val scope = rememberCoroutineScope()
    val user by chatViewModel.user.collectAsState()

    val isCompactWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isShortHeight = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    val usePhoneLayout = isCompactWidth || isShortHeight

    val keyboardController = LocalSoftwareKeyboardController.current

    val isConversationCreator by chatViewModel.isConversationCreator.collectAsState()
    val isOtherUserBlocked by chatViewModel.isBlockedByOtherUser.collectAsState()

    LaunchedEffect(selectedConversationId) {
        selectedConversationId?.let { conversationId ->
            chatViewModel.loadConversation(conversationId)
        }
    }

    LaunchedEffect(
        selectedConversationId,
        messages.size,
        windowSizeClass.widthSizeClass,
        windowSizeClass.heightSizeClass
    ) {
        delay(100.milliseconds)
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    val isBlockedByOtherUser by chatViewModel.isBlockedByOtherUser.collectAsState()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            chatNavigationSuiteItems(
                currentRoute = currentRoute,
                unreadMessages = unreadMessages,
                pendingRequests = pendingRequests.size,
                onDestinationClick = { route ->
                    when (route) {
                        Screens.Home.route -> onNavigateToHome()
                        Screens.Contacts.route -> onNavigateToContacts()
                        Screens.Calls.route -> onNavigateToCalls()
                        Screens.MyProfile.route -> onNavigateToProfile()
                    }
                },
                itemColors = itemColors
            )
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = navigationContainerColor,
            navigationRailContainerColor = navigationContainerColor,
            navigationDrawerContainerColor = navigationContainerColor
        ),
        layoutType = customNavSuiteType
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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error")
                    }
                }

                HomeUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Loading")
                    }
                }

                is HomeUiState.Success -> {
                    when {
                        usePhoneLayout -> {
                            LeftPane(
                                modifier = modifier
                                    .padding(paddingValues)
                                    .padding(vertical = 8.dp),
                                user = state.user,
                                categories = state.categories,
                                selectedCategory = selectedCategory,
                                onSelectCategory = homeViewModel::selectCategory,
                                onConversationClick = onConversationClick,
                                onClickToSeeAll = {},
                                conversations = state.conversations,
                                focusManager = focusManager,
                                onLogoutClick = {
                                    authViewModel.logout()
                                    onNavigateToLoginScreen()
                                },
                                onNavigateToProfile = onNavigateToProfile,
                                isBlockedByOtherUser = isBlockedByOtherUser
                            )
                        }

                        else -> {
                            AutoSelectFirstConversation(
                                conversations = state.conversations,
                                selectedConversationId = selectedConversationId,
                                onConversationSelected = { selectedConversationId = it },
                                isCompact = false
                            )
                            NavigableListDetailPaneScaffold(
                                navigator = navigator,
                                listPane = {
                                    AnimatedPane {
                                        LeftPane(
                                            modifier = modifier
                                                .padding(paddingValues)
                                                .padding(vertical = 8.dp),
                                            user = state.user,
                                            categories = state.categories,
                                            selectedCategory = selectedCategory,
                                            onSelectCategory = homeViewModel::selectCategory,
                                            onConversationClick = { conversationId ->
                                                selectedConversationId = conversationId

                                                scope.launch {
                                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                                }
                                            },
                                            onClickToSeeAll = {},
                                            conversations = state.conversations,
                                            focusManager = focusManager,
                                            onLogoutClick = {
                                                authViewModel.logout()
                                                onNavigateToLoginScreen()
                                            },
                                            onNavigateToProfile = onNavigateToProfile,
                                            isBlockedByOtherUser = isBlockedByOtherUser,
                                        )
                                    }
                                },
                                detailPane = {
                                    AnimatedPane {
                                        RightPane(
                                            user = user,
                                            messages = messages,
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
                                            isPeekEnabled = isPeekEnabled,
                                            onPeekClick = { isPeekEnabled = !isPeekEnabled },
                                            typingUserIds = listOf(),
                                            updateTypingStatus = { },
                                            isBlockedByOtherUser = isOtherUserBlocked,
                                        )
                                    }
                                },
                                extraPane = {
                                    AnimatedPane {
                                        ExtraPane(
                                            user = state.user,
                                            onDeleteConversationClick = {},
                                            isConversationCreator = isConversationCreator,
                                        )
                                    }
                                }
                            )
                        }
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
        isGroup = false
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            LeftPane(
                modifier = Modifier.padding(paddingValues),
                user = user,
                categories = categories,
                selectedCategory = "All",
                onSelectCategory = {},
                onConversationClick = { },
                onClickToSeeAll = {},
                conversations = List(3) { conversation },
                focusManager = LocalFocusManager.current,
                onLogoutClick = {},
                onNavigateToProfile = {},
                isBlockedByOtherUser = false,
            )
        }
    }
}

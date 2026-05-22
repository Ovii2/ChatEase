package com.example.chatease.presentation.screens.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.screens.shared.chat.ChatBottomBar
import com.example.chatease.presentation.screens.shared.panes.left_pane.LeftPane
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.state.HomeUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AuthViewModel
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel
import com.example.chatease.presentation.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
    onNavigateToLoginScreen: () -> Unit,
    onStartNewChat: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()

    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)

    val pendingRequests by contactsViewModel.pendingRequests.collectAsState()
    val showContactsBadge = pendingRequests.isNotEmpty()

    Scaffold(
        bottomBar = {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                ChatBottomBar(
                    currentRoute = Screens.Home.route,
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToContacts = onNavigateToContacts,
                    onStartNewChat = onStartNewChat,
                    onNavigateToCalls = onNavigateToCalls,
                    onNavigateToProfile = onNavigateToProfile,
                    pendingRequests = pendingRequests.size,
                    showContactsBadge = showContactsBadge,
                )
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            HomeUiState.Loading -> {

            }

            is HomeUiState.Success -> {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> HomeScreenCompactLayout(
                        paddingValues = paddingValues,
                        user = state.user,
                        categories = state.categories,
                        selectedCategory = selectedCategory,
                        onSelectCategory = homeViewModel::selectCategory,
                        conversations = state.conversations,
                        onClickToSeeAll = {},
                        onConversationClick = onConversationClick,
                        focusManager = focusManager,
                        onLogoutClick = {
                            authViewModel.logout()
                            onNavigateToLoginScreen()
                        }
                    )
                }
            }

            is HomeUiState.Error -> {

            }
        }
    }
}

@Composable
fun HomeScreenCompactLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    user: User,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    conversations: List<Conversation>,
    onClickToSeeAll: () -> Unit,
    onConversationClick: (String) -> Unit,
    focusManager: FocusManager,
    onLogoutClick: () -> Unit
) {
    LeftPane(
        modifier = modifier
            .padding(paddingValues)
            .padding(top = 8.dp),
        user = user,
        categories = categories,
        selectedCategory = selectedCategory,
        onSelectCategory = onSelectCategory,
        onConversationClick = onConversationClick,
        onClickToSeeAll = onClickToSeeAll,
        conversations = conversations,
        focusManager = focusManager,
        onLogoutClick = onLogoutClick
    )
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

    val conversation = Conversation(
        id = "1",
        participants = listOf(user),
        lastMessage = "Hey! Are we still for lunch?",
        timestamp = System.currentTimeMillis(),
        unreadCount = 2
    )
    ChatEaseTheme() {
        HomeScreenCompactLayout(
            paddingValues = PaddingValues(),
            user = user,
            categories = categories,
            selectedCategory = "All",
            onSelectCategory = {},
            conversations = listOf(conversation),
            onClickToSeeAll = {},
            onConversationClick = {},
            focusManager = LocalFocusManager.current,
            onLogoutClick = {}
        )
    }
}

package com.example.chatease.presentation.ui.screens.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.LeftPane
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
    onStartNewChat: () -> Unit,
    currentRoute: String
) {
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
        layoutType = customNavSuiteType
    ) {
        Scaffold(floatingActionButton = {
            StartChatFab(
                modifier = Modifier.padding(8.dp),
                onStartNewChat = onStartNewChat
            )
        }) { paddingValues ->
            when (val state = uiState) {
                is HomeUiState.Error -> {}
                HomeUiState.Loading -> {}
                is HomeUiState.Success -> {
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
                        onNavigateToProfile = onNavigateToProfile
                    )
                }
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
                onNavigateToProfile = {}
            )
        }
    }
}

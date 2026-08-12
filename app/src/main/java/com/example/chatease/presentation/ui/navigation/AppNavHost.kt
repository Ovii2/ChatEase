package com.example.chatease.presentation.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chatease.presentation.ui.screens.add_members.AddMembersScreen
import com.example.chatease.presentation.ui.screens.all_requests.AllRequestsScreen
import com.example.chatease.presentation.ui.screens.audio_call.AudioCallScreen
import com.example.chatease.presentation.ui.screens.blocked_users.BlockedUsersScreen
import com.example.chatease.presentation.ui.screens.calls.CallsScreen
import com.example.chatease.presentation.ui.screens.chat.ChatScreen
import com.example.chatease.presentation.ui.screens.chat_info.ChatInfoScreen
import com.example.chatease.presentation.ui.screens.contacts.ContactsScreen
import com.example.chatease.presentation.ui.screens.group_chat.GroupChatScreen
import com.example.chatease.presentation.ui.screens.group_chat_info.GroupChatInfoScreen
import com.example.chatease.presentation.ui.screens.group_chat_members.GroupChatMembersScreen
import com.example.chatease.presentation.ui.screens.home.HomeScreen
import com.example.chatease.presentation.ui.screens.login.LoginScreen
import com.example.chatease.presentation.ui.screens.media_and_docs.MediaAndDocsScreen
import com.example.chatease.presentation.ui.screens.my_profile.MyProfileScreen
import com.example.chatease.presentation.ui.screens.new_chat.NewChatScreen
import com.example.chatease.presentation.ui.screens.new_chat_group.NewChatGroupScreen
import com.example.chatease.presentation.ui.screens.other_user_profile.OtherUserProfileScreen
import com.example.chatease.presentation.ui.screens.privacy_security.PrivacyAndSecurityScreen
import com.example.chatease.presentation.ui.screens.sent_requests.SentRequestsScreen
import com.example.chatease.presentation.ui.screens.sign_up.SignUpScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavHost(
    paddingValues: PaddingValues,
    navController: NavHostController,
    onThemeToggleClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val auth = Firebase.auth
    val startDestination = if (auth.currentUser != null) Screens.Home.route else Screens.Login.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screens.Home.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screens.SignUp.route) {
            SignUpScreen(
                onNavigateToLoginScreen = {
                    navController.navigate(Screens.Login.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screens.Login.route) {
            LoginScreen(
                paddingValues = paddingValues,
                onNavigateToSignUpScreen = {
                    navController.navigate(Screens.SignUp.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screens.Home.route) {
            HomeScreen(
                onNavigateToHome = {
                    if (navController.currentDestination?.route != Screens.Home.route) {
                        navController.navigate(Screens.Home.route) {
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToContacts = {
                    navController.navigate(Screens.Contacts.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCalls = {
                    navController.navigate(Screens.Calls.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screens.MyProfile.route) {
                        launchSingleTop = true
                    }
                },
                onConversationClick = { conversationId, isGroup ->
                    if (isGroup) {
                        navController.navigate(Screens.GroupChat.createRoute(conversationId))
                    } else
                        navController.navigate(Screens.Chat.createRoute(conversationId))
                },
                onNavigateToLoginScreen = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onStartNewChat = {
                    navController.navigate(Screens.NewChat.route) {
                        launchSingleTop = true
                    }
                },
                currentRoute = currentRoute,
                onBackClick = { navController.popBackStack() },
                onNavigateToChatInfo = { conversationId ->
                    navController.navigate(Screens.ChatInfo.createRoute(conversationId)) {
                        launchSingleTop = true
                    }
                },
                onViewContactClick = {},
                snackbarHostState = snackbarHostState,
            )
        }
        composable(route = Screens.Chat.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable

            ChatScreen(
                conversationId = conversationId,
                onBackClick = { navController.popBackStack() },
                onNavigateToChatInfo = {
                    navController.navigate(
                        Screens.ChatInfo.createRoute(
                            conversationId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToAudioCallScreen = { callId ->
                    navController.navigate(Screens.AudioCall.createRoute(callId)) {
                        launchSingleTop = true
                    }
                },
                snackbarHostState = snackbarHostState,
            )
        }
        composable(route = Screens.NewChat.route) {
            NewChatScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToChatScreen = {
                    navController.navigate(Screens.Chat.createRoute(it)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToNewGroupScreen = { selectedUserIds ->
                    val selectedIds = selectedUserIds.joinToString(",")
                    navController.navigate(Screens.NewChatGroup.createRoute(selectedIds)) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.Contacts.route) {
            ContactsScreen(
                onNavigateToSentRequests = {
                    navController.navigate(Screens.SentRequests.route) {
                        launchSingleTop = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onNavigateToAllRequests = {
                    navController.navigate(Screens.AllRequests.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToOtherUserProfile = {
                    navController.navigate(Screens.OtherUserProfile.createRoute(it)) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.SentRequests.route) {
            SentRequestsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Screens.AllRequests.route) {
            AllRequestsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Screens.MyProfile.route) {
            MyProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToLoginScreen = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onThemeToggleClick = onThemeToggleClick,
                onNavigateToPrivacyAndSecurity = {
                    navController.navigate(Screens.PrivacyAndSecurity.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.ChatInfo.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable

            ChatInfoScreen(
                conversationId = conversationId,
                onBackClick = { navController.popBackStack() },
                onNavigateToHomeScreen = {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onViewContactClick = { userId ->
                    navController.navigate(Screens.OtherUserProfile.createRoute(userId)) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.OtherUserProfile.route) {
            val userId = it.arguments?.getString("userId") ?: return@composable

            OtherUserProfileScreen(
                onBackClick = { navController.popBackStack() },
                userId = userId,
                onNavigateToChatScreen = { conversationId ->
                    navController.navigate(Screens.Chat.createRoute(conversationId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToMutualGroup = { conversationId ->
                    navController.navigate(Screens.GroupChat.createRoute(conversationId))
                },
            )
        }
        composable(route = Screens.BlockedUsers.route) {
            BlockedUsersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Screens.Calls.route) {
            CallsScreen(
                onBackClick = { navController.popBackStack() },
                currentRoute = currentRoute,
                onNavigateToHome = {
                    navController.navigate(Screens.Home.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToContacts = {
                    navController.navigate(Screens.Contacts.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screens.MyProfile.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.AudioCall.route) {
            val callId = it.arguments?.getString("callId") ?: return@composable

            AudioCallScreen(
                callId = callId,
                onNavigateBack = { navController.popBackStack() },
                currentRoute = currentRoute
            )
        }
        composable(route = Screens.PrivacyAndSecurity.route) {
            PrivacyAndSecurityScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToBlockedUsers = {
                    navController.navigate(Screens.BlockedUsers.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screens.NewChatGroup.route) { backStackEntry ->
            val selectedUserIds = backStackEntry.arguments
                ?.getString("selectedUserIds")
                ?.split(",")
                ?.filter { it.isNotBlank() }
                .orEmpty()

            NewChatGroupScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToGroupChat = {
                    navController.navigate(Screens.GroupChat.createRoute(it)) {
                        popUpTo(Screens.NewChat.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                selectedUserIds = selectedUserIds
            )
        }
        composable(route = Screens.GroupChat.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable

            GroupChatScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                conversationId = conversationId,
                onNavigateToGroupChatInfo = {
                    navController.navigate(
                        Screens.GroupChatInfo.createRoute(conversationId)
                    ) { launchSingleTop = true }
                },
                snackbarHostState = snackbarHostState,
            )
        }
        composable(route = Screens.GroupChatInfo.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable

            GroupChatInfoScreen(
                onBackClick = { navController.popBackStack() },
                conversationId = conversationId,
                onNavigateToMembersScreen = {
                    navController.navigate(Screens.GroupChatMembers.createRoute(conversationId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateHomeScreen = {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.GroupChatMembers.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable

            GroupChatMembersScreen(
                onBackClick = { navController.popBackStack() },
                conversationId = conversationId,
                snackbarHostState = snackbarHostState,
                onNavigateToAddMembersScreen = {
                    navController.navigate(Screens.AddMembers.createRoute(conversationId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToProfileScreen = { userId ->
                    navController.navigate(Screens.OtherUserProfile.createRoute(userId)) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = Screens.AddMembers.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable

            AddMembersScreen(
                onBackClick = { navController.popBackStack() },
                conversationId = conversationId,
            )
        }
        composable(route = Screens.MediaAndDocs.route) {
            MediaAndDocsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

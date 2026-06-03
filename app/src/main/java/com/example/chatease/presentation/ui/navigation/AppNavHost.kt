package com.example.chatease.presentation.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatease.presentation.ui.screens.all_requests.AllRequestsScreen
import com.example.chatease.presentation.ui.screens.chat.ChatScreen
import com.example.chatease.presentation.ui.screens.contacts.ContactsScreen
import com.example.chatease.presentation.ui.screens.home.HomeScreen
import com.example.chatease.presentation.ui.screens.login.LoginScreen
import com.example.chatease.presentation.ui.screens.my_profile.MyProfileScreen
import com.example.chatease.presentation.ui.screens.new_chat.NewChatScreen
import com.example.chatease.presentation.ui.screens.sent_requests.SentRequestsScreen
import com.example.chatease.presentation.ui.screens.sign_up.SignUpScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavHost(
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val auth = Firebase.auth
    val startDestination = if (auth.currentUser != null) Screens.Home.route else Screens.Login.route

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
                onNavigateToCalls = {},
                onNavigateToProfile = {
                    navController.navigate(Screens.MyProfile.route) {
                        launchSingleTop = true
                    }
                },
                onConversationClick = { conversationId ->
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
                }
            )
        }
        composable(route = Screens.Chat.route) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable
            ChatScreen(
                conversationId = conversationId,
                onBackClick = { navController.popBackStack() },
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
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
package com.example.chatease.presentation.ui.navigation

sealed class Screens(val route: String) {

    data object Login : Screens("login")
    data object SignUp : Screens("sign_up")
    data object Home : Screens("home")
    data object Chat : Screens("chat/{conversationId") {
        fun createRoute(conversationId: String): String {
            return "chat/$conversationId"
        }
    }

    data object NewChat : Screens("new_chat")
    data object Contacts : Screens("contacts")
    data object SentRequests : Screens("sent_requests")
    data object Calls : Screens("calls")
    data object Profile : Screens("profile")
}
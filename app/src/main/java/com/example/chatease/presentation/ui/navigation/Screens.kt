package com.example.chatease.presentation.ui.navigation

import androidx.annotation.StringRes
import com.example.chatease.R

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
    data object AllRequests : Screens("all_requests")
    data object Calls : Screens("calls")
    data object Profile : Screens("profile")
}

@StringRes
fun Screens.toScreenName(): Int {
    return when (this) {
        Screens.Login -> R.string.login
        Screens.SignUp -> R.string.sign_up
        Screens.Home -> R.string.contacts
        Screens.Chat -> R.string.new_chat
        Screens.NewChat -> R.string.new_chat
        Screens.Contacts -> R.string.contacts
        Screens.SentRequests -> R.string.sent_requests
        Screens.AllRequests -> R.string.all_requests
        Screens.Calls -> R.string.calls
        Screens.Profile -> R.string.profile
    }
}
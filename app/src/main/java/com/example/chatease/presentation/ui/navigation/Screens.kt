package com.example.chatease.presentation.ui.navigation

import androidx.annotation.StringRes
import com.example.chatease.R

sealed class Screens(val route: String) {

    data object Login : Screens("login")
    data object SignUp : Screens("sign_up")
    data object Home : Screens("home")
    data object Chat : Screens("chat/{conversationId}") {
        fun createRoute(conversationId: String): String {
            return "chat/$conversationId"
        }
    }

    data object NewChat : Screens("new_chat")
    data object Contacts : Screens("contacts")
    data object SentRequests : Screens("sent_requests")
    data object AllRequests : Screens("all_requests")
    data object Calls : Screens("calls")
    data object MyProfile : Screens("my_profile")
    data object OtherUserProfile : Screens("profile/{userId}") {
        fun createRoute(userId: String): String = "profile/$userId"
    }

    data object ChatInfo : Screens("chat_info/{conversationId}") {
        fun createRoute(conversationId: String): String {
            return "chat_info/$conversationId"
        }
    }

    data object BlockedUsers : Screens("blocked_users")

    data object AudioCall : Screens("audio_call/{callId}") {
        fun createRoute(callId: String) = "audio_call/$callId"
    }

    data object PrivacyAndSecurity : Screens("privacy_and_security")
    data object NewChatGroup : Screens("new_chat_group/{selectedUserIds}") {
        fun createRoute(selectedUserIds: String) = "new_chat_group/${selectedUserIds}"
    }

    data object GroupChat : Screens("group_chat/{conversationId}") {
        fun createRoute(conversationId: String) = "group_chat/${conversationId}"
    }

    data object GroupChatInfo : Screens("group_chat_info/{conversationId}") {
        fun createRoute(conversationId: String) = "group_chat_info/${conversationId}"
    }

    data object GroupChatMembers : Screens("group_chat_members/{conversationId}") {
        fun createRoute(conversationId: String) = "group_chat_members/${conversationId}"
    }
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
        Screens.MyProfile -> R.string.my_profile
        Screens.ChatInfo -> R.string.chat_info
        Screens.OtherUserProfile -> R.string.profile
        Screens.BlockedUsers -> R.string.blocked_users
        Screens.AudioCall -> R.string.audio_call
        Screens.PrivacyAndSecurity -> R.string.privacy_security
        Screens.NewChatGroup -> R.string.new_chat_group
        Screens.GroupChat -> R.string.group_chat
        Screens.GroupChatInfo -> R.string.group_info
        Screens.GroupChatMembers -> R.string.members
    }
}
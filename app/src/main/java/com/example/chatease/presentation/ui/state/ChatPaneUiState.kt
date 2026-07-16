package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User

sealed class ChatPaneUiState {

    data class DirectChat(
        val user: User
    ) : ChatPaneUiState()

    data class GroupChat(
        val group: Group,
        val members: List<User>
    ) : ChatPaneUiState()

}

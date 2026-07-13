package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User

sealed class GroupChatUiState {

    object Loading : GroupChatUiState()
    data class Success(
        val group: Group,
        val members: List<User>,
        val messages: List<Message>
    ) : GroupChatUiState()

    data class Error(
        val message: String
    ) : GroupChatUiState()
}
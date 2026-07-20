package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.User

sealed class GroupChatMembersUiState {
    data object Loading : GroupChatMembersUiState()

    data class Success(
        val members: List<User>,
        val adminIds: List<String>,
        val ownerId: String
    ) : GroupChatMembersUiState()

    data class Error(
        val message: String
    ) : GroupChatMembersUiState()
}

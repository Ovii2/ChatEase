package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User

sealed class GroupChatInfoUiState {

    object Loading : GroupChatInfoUiState()

    data class Success(
        val group: Group,
        val members: List<User>,
    ) : GroupChatInfoUiState()

    data class Error(val error: String) : GroupChatInfoUiState()
}
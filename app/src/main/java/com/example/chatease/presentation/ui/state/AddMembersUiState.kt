package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.User

sealed class AddMembersUiState {

    data object Loading : AddMembersUiState()
    data class Success(
        val members: List<User>,
        val selectedMemberIds: Set<String>
    ) : AddMembersUiState()

    data class Error(
        val message: String
    ) : AddMembersUiState()
}
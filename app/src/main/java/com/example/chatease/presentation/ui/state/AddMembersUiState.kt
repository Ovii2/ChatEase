package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.User

sealed class AddMembersUiState {

    data object Loading : AddMembersUiState()
    data class Success(
        val members: List<User>
    ) : AddMembersUiState()

    data class Error(
        val message: String
    ) : AddMembersUiState()
}
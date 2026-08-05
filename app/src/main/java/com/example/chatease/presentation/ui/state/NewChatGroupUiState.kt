package com.example.chatease.presentation.ui.state

sealed class NewChatGroupUiState {

    object Idle : NewChatGroupUiState()
    object Loading : NewChatGroupUiState()
    object Success : NewChatGroupUiState()
    data class Error(val message: String) : NewChatGroupUiState()
}
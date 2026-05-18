package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User

sealed class HomeUiState {
    
    object Loading : HomeUiState()
    data class Success(
        val user: User = User(),
        val categories: List<Category> = emptyList(),
        val conversations: List<Conversation> = emptyList()
    ) : HomeUiState()

    data class Error(
        val message: String
    ) : HomeUiState()
}
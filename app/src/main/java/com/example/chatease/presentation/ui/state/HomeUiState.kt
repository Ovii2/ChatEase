package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.Category
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User = User(),
    val categories: List<Category> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
    val errorMessage: String? = null
)

package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.User

data class ContactsUiState(
    val contacts: List<User> = emptyList(),
    val pendingRequests: List<User> = emptyList(),
    val sentRequests: List<User> = emptyList()
)

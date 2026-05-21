package com.example.chatease.presentation.ui.model

import com.example.chatease.domain.model.User

data class PendingRequestUiModel(
    val requestId: String,
    val user: User
)

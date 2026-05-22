package com.example.chatease.presentation.ui.model

import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus

data class SentRequestUiModel(
    val requestId: String,
    val receiver: User,
    val status: ContactRequestStatus
)

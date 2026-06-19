package com.example.chatease.presentation.ui.model

import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.User

data class CallHistoryUiModel(
    val callHistory: CallHistory,
    val user: User
)

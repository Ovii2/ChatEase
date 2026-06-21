package com.example.chatease.presentation.ui.model

import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallDirection

data class CallHistoryUiModel(
    val callHistory: CallHistory,
    val user: User,
    val callDirection: CallDirection
)

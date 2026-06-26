package com.example.chatease.presentation.ui.state

import com.example.chatease.presentation.ui.model.CallHistoryUiModel

sealed class CallsUiState {

    object Loading : CallsUiState()

    data class Success(
        val callHistoryList: List<CallHistoryUiModel>
    ) : CallsUiState()

    data class Error(
        val message: String
    ) : CallsUiState()
}
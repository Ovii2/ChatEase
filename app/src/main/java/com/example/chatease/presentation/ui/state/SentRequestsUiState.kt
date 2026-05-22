package com.example.chatease.presentation.ui.state

import com.example.chatease.presentation.ui.model.SentRequestUiModel

sealed class SentRequestsUiState {
    data object Loading : SentRequestsUiState()

    data object Empty : SentRequestsUiState()

    data class Success(
        val requests: List<SentRequestUiModel>
    ) : SentRequestsUiState()

    data class Error(val message: String) : SentRequestsUiState()
}
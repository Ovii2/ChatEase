package com.example.chatease.presentation.ui.state

import androidx.annotation.StringRes
import com.example.chatease.presentation.ui.model.PendingRequestUiModel

sealed class ReceivedRequestsUiState {
    data object Loading : ReceivedRequestsUiState()

    data object Empty : ReceivedRequestsUiState()

    data class Success(
        val requests: List<PendingRequestUiModel>
    ) : ReceivedRequestsUiState()

    data class Error(@StringRes val errorMessage: Int) : ReceivedRequestsUiState()
}

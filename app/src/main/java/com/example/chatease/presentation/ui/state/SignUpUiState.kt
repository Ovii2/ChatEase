package com.example.chatease.presentation.ui.state

import androidx.annotation.StringRes

sealed class SignUpUiState {

    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    object Success : SignUpUiState()
    data class Error(
        val message: String? = null,
        @StringRes val messageRes: Int? = null
    ) :
        SignUpUiState()
}
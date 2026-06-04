package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.User
import com.example.chatease.presentation.ui.model.ProfileStatUiModel

sealed class MyProfileUiState {

    data object Loading : MyProfileUiState()

    data class Success(
        val user: User,
        val stats: List<ProfileStatUiModel>,
        val isUploadingImage: Boolean = false
    ) : MyProfileUiState()

    data class Error(
        val message: String
    ) : MyProfileUiState()
}

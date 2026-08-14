package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.MediaItem

sealed class MediaAndDocsUiState {

    data object Loading : MediaAndDocsUiState()
    data class Success(
        val mediaItems: List<MediaItem>
    ) : MediaAndDocsUiState()

    data class Error(
        val message: String
    ) : MediaAndDocsUiState()
}
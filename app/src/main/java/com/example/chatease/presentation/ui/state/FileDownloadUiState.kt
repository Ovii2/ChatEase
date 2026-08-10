package com.example.chatease.presentation.ui.state

import com.example.chatease.domain.model.enums.FileDownloadState

data class FileDownloadUiState(
    val messageId: String? = null,
    val state: FileDownloadState = FileDownloadState.IDLE
)

package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.enums.FileDownloadState
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.presentation.ui.state.MediaAndDocsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaAndDocsViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MediaAndDocsUiState>(MediaAndDocsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _downloadState = MutableStateFlow(FileDownloadState.IDLE)
    val downloadState = _downloadState.asStateFlow()

    fun loadMediaItems(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = MediaAndDocsUiState.Loading
            try {
                val items = fileRepository.getMediaItems(conversationId)
                _uiState.value = MediaAndDocsUiState.Success(mediaItems = items)
            } catch (e: Exception) {
                _uiState.value = MediaAndDocsUiState.Error(
                    message = e.message ?: "Failed to load media items"
                )
            }
        }
    }

    fun downloadDoc(
        fileUrl: String,
        filename: String,
        mimeType: String
    ) {
        viewModelScope.launch {
            _downloadState.value = FileDownloadState.DOWNLOADING
            try {
                fileRepository.saveFileToDownloads(
                    fileUrl = fileUrl,
                    fileName = filename,
                    mimeType = mimeType
                )
                _downloadState.value = FileDownloadState.SUCCESS
            } catch (e: Exception) {
                _downloadState.value = FileDownloadState.FAILED
                Log.v("MediaAndDocsViewModel", e.message ?: "Failed to download item", e)
            }
        }
    }

}
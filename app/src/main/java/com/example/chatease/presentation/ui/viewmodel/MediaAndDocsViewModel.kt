package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

}
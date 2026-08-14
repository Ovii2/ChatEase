package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaAndDocsViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems = _mediaItems.asStateFlow()

    fun loadMediaItems(conversationId: String) {
        viewModelScope.launch {
            try {
                _mediaItems.value = fileRepository.getMediaItems(conversationId)

            } catch (e: Exception) {
                Log.v("MediaAndDocsViewModel", e.message ?: "Failed to load media items", e)
            }
        }
    }
}
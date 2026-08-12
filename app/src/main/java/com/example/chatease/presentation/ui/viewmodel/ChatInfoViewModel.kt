package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository,
    private val groupRepository: GroupRepository,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _isConversationCreator = MutableStateFlow(false)
    val isConversationCreator = _isConversationCreator.asStateFlow()

    private val _isBlockedByMe = MutableStateFlow(false)
    val isBlockedByMe = _isBlockedByMe.asStateFlow()

    private val _isBlockedByOtherUser = MutableStateFlow(false)
    val isBlockedByOtherUser = _isBlockedByOtherUser.asStateFlow()

    private val _isConversationDeleted = MutableStateFlow(false)
    val isConversationDeleted = _isConversationDeleted.asStateFlow()

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems = _mediaItems.asStateFlow()

    private var observeUserJob: Job? = null

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val conversation = conversationRepository.getConversation(conversationId)
                _isConversationCreator.value = conversation.creatorId == currentUserId
                val otherUserId = conversation.participantIds.first { it != currentUserId }
                _isBlockedByMe.value = userRepository.isUserBlocked(otherUserId)
                _isBlockedByOtherUser.value = userRepository.isBlockedByUser(otherUserId)
                observeUser(otherUserId)
            } catch (e: Exception) {
                Log.v("ChatInfoViewModel", e.message ?: "Failed to load conversation")
            }
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.blockUser(userId)
                _isBlockedByMe.value = true
            } catch (e: Exception) {
                Log.v("ChatInfoViewModel", e.message ?: "Failed to block user")
            }
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.unblockUser(userId)
                _isBlockedByMe.value = false
            } catch (e: Exception) {
                Log.v("ChatInfoViewModel", e.message ?: "Failed to unblock user")
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                conversationRepository.deleteConversation(conversationId)
                _isConversationDeleted.value = true
            } catch (e: Exception) {
                Log.e("ChatInfoViewModel", e.message ?: "Failed to delete conversation")
            }
        }
    }

    fun deleteGroupConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                conversationRepository.deleteConversation(conversationId)
                groupRepository.removeFormerMemberVisibility(
                    conversationId = conversationId,
                    currentUserId = currentUserId
                )
                _isConversationDeleted.value = true
            } catch (e: Exception) {
                _isConversationDeleted.value = false
                Log.e("ChatInfoViewModel", e.message ?: "Failed to delete group conversation")
            }
        }
    }

    fun loadMediaItems(conversationId: String) {
        viewModelScope.launch {
            try {
                val mediaItems = fileRepository.getMediaItems(conversationId)

                _mediaItems.value = mediaItems.map { item ->
                    val user = userRepository.getUserById(item.senderId)

                    item.copy(
                        senderName = user.fullName
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to load media items", e)
            }
        }
    }

    private fun observeUser(userId: String) {
        observeUserJob?.cancel()

        observeUserJob = viewModelScope.launch {
            try {
                userRepository.observeUser(userId)
                    .collect { user ->
                        _user.value = user
                        _isBlockedByOtherUser.value = currentUserId in user.blockedUserIds
                    }
            } catch (e: Exception) {
                Log.v("ChatInfoViewModel", e.message ?: "Failed to load user")
            }
        }
    }

}

package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _isUserConnected = MutableStateFlow(false)
    val isUserConnected = _isUserConnected.asStateFlow()

    private val _isUserBlocked = MutableStateFlow(false)
    val isUserBlocked = _isUserBlocked.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.observeUser(userId)
                    .collect { user ->
                        _user.value = user
                    }
            } catch (e: Exception) {
                Log.v("OtherUserProfileViewModel", e.message ?: "Failed to load user profile")
            }
        }
    }

    fun checkIfUserConnected(otherUserId: String) {
        viewModelScope.launch {
            try {
                _isUserConnected.value = userRepository.isUserConnected(otherUserId)
            } catch (e: Exception) {
                Log.v(
                    "OtherUserProfileViewModel",
                    e.message ?: "Failed to check if user is connected"
                )
            }
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.blockUser(userId)
                _isUserBlocked.value = true
            } catch (e: Exception) {
                Log.v("OtherUserProfileViewModel", e.message ?: "Failed to block user")
            }
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.unblockUser(userId)
                _isUserBlocked.value = false
            } catch (e: Exception) {
                Log.v("OtherUserProfileViewModel", e.message ?: "Failed to unblock user")
            }
        }
    }

    fun checkIfUserIsBlocked(userId: String) {
        viewModelScope.launch {
            try {
                _isUserBlocked.value = userRepository.isUserBlocked(userId)
            } catch (e: Exception) {
                Log.v("OtherUserProfileViewModel", e.message ?: "Failed to check for blocked user")
            }
        }
    }

    fun createNewConversation(selectedUserId: String, onConversationCreated: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val participantIds = listOf(currentUserId, selectedUserId).sorted()

                val conversationId =
                    conversationRepository.getExistingConversationId(participantIds)
                        ?: conversationRepository.createConversation(
                            participantIds,
                            ConversationType.DIRECT
                        )

                onConversationCreated(conversationId)
            } catch (e: Exception) {
                Log.e("OtherUserProfileViewModel", e.message ?: "Failed to start conversation")
            }
        }
    }

}

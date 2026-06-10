package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _isConversationCreator = MutableStateFlow(false)
    val isConversationCreator = _isConversationCreator.asStateFlow()

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val conversation = conversationRepository.getConversation(conversationId)
                _isConversationCreator.value = conversation.creatorId == currentUserId
                val otherUserId = conversation.participantIds.first { it != currentUserId }
                observeUser(otherUserId)
            } catch (e: Exception) {
                Log.v("ChatInfoViewModel", e.message ?: "Failed to load conversation")
            }
        }
    }

    private suspend fun observeUser(userId: String) {
        try {
            userRepository.observeUser(userId)
                .collect { user ->
                    _user.value = user
                }
        } catch (e: Exception) {
            Log.v("ChatInfoViewModel", e.message ?: "Failed to load user")
        }
    }


}

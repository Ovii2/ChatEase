package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Message
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
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()


    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                val conversation = conversationRepository.getConversation(conversationId)
                val otherUserId = conversation.participantIds.first {
                    it != currentUserId
                }
                val otherUser = userRepository.getUserById(otherUserId)
                _user.value = otherUser
                _messages.value = conversationRepository.getMessages(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to load conversation")
            }
        }
    }
}
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

    val firstUnreadMessageId: String?
        get() = _messages.value.firstOrNull { message ->
            currentUserId !in message.seenBy
        }?.messageId

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                val conversation = conversationRepository.getConversation(conversationId)
                val otherUserId = conversation.participantIds.first {
                    it != currentUserId
                }
                observeUser(otherUserId)
                observeMessages(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to load conversation")
            }
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val message = Message(
                    conversationId = conversationId,
                    senderId = currentUserId,
                    text = text.trim(),
                    timeStamp = System.currentTimeMillis(),
                    seenBy = listOf(currentUserId)
                )
                conversationRepository.sendMessage(message)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to send message")
            }
        }
    }

    fun markMessagesAsSeen(conversationId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                conversationRepository.markMessagesAsSeen(conversationId, currentUserId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to mark message as seen")
            }
        }
    }

    fun addReactionToMessage(conversationId: String, messageId: String, reaction: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                conversationRepository.addReactionToMessage(
                    conversationId = conversationId,
                    messageId = messageId,
                    userId = currentUserId,
                    reaction = reaction
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to add reaction to message")
            }
        }
    }

    private fun observeMessages(conversationId: String) {
        viewModelScope.launch {
            conversationRepository.observeMessages(conversationId)
                .collect { messages ->
                    _messages.value = messages
                }
        }
    }

    private fun observeUser(userId: String) {
        viewModelScope.launch {
            userRepository.observeUser(userId)
                .collect { user ->
                    _user.value = user
                }
        }
    }
}
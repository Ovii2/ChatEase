package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
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

    private val _isConversationDeleted = MutableStateFlow(false)
    val isConversationDeleted = _isConversationDeleted.asStateFlow()

    private val _isConversationCreator = MutableStateFlow(false)
    val isConversationCreator = _isConversationCreator.asStateFlow()

    private val _typingUserIds = MutableStateFlow<List<String>>(emptyList())
    val typingUserIds = _typingUserIds.asStateFlow()

    private val _isBlockedByOtherUser = MutableStateFlow(false)
    val isBlockedByOtherUser = _isBlockedByOtherUser.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    val firstUnreadMessageId: String?
        get() = _messages.value.firstOrNull { message ->
            currentUserId !in message.seenBy && message.senderId != currentUserId
        }?.messageId

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                val conversation = conversationRepository.getConversation(conversationId)
                val loggedInUserId = auth.currentUser?.uid ?: return@launch
                _isConversationCreator.value = conversation.creatorId == loggedInUserId
                val otherUserId = conversation.participantIds.first {
                    it != loggedInUserId
                }
                observeIsBlockedByOtherUser(otherUserId)
                observeUser(otherUserId)
                observeMessages(conversationId)
                observeConversation(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to load conversation")
            }
        }
    }

    fun sendMessage(conversationId: String, text: String, repliedMessage: Message?) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val message = Message(
                    conversationId = conversationId,
                    senderId = currentUserId,
                    text = text.trim(),
                    timeStamp = System.currentTimeMillis(),
                    seenBy = listOf(currentUserId),
                    messageType = MessageType.TEXT,
                    replyMessage = repliedMessage?.let { message ->
                        ReplyMessage(
                            messageId = message.messageId,
                            senderId = message.senderId,
                            text = message.text
                        )
                    }
                )
                conversationRepository.sendMessage(message)
                conversationRepository.updateTypingStatus(conversationId, currentUserId, false)
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

    fun deleteConversationIfEmpty(conversationId: String) {
        viewModelScope.launch {
            try {
                conversationRepository.deleteIfEmptyConversation(conversationId)
                _isConversationDeleted.value = true
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to delete conversation")
            }
        }
    }

    fun updateTypingStatus(conversationId: String, isTyping: Boolean) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                conversationRepository.updateTypingStatus(
                    conversationId = conversationId,
                    userId = userId,
                    isTyping = isTyping
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to update typing status")
            }
        }
    }

    fun checkIfUserIsBlockedByOtherUser(otherUserId: String) {
        viewModelScope.launch {
            try {
                _isBlockedByOtherUser.value = userRepository.isBlockedByUser(otherUserId)
            } catch (e: Exception) {
                Log.v(
                    "ChatViewModel",
                    e.message ?: "Failed to check if user is blocked by other user"
                )
            }
        }
    }

    private fun observeIsBlockedByOtherUser(otherUserId: String) {
        viewModelScope.launch {
            userRepository.observeIsBlockedByUser(otherUserId)
                .collect { isBlocked ->
                    _isBlockedByOtherUser.value = isBlocked
                }
        }
    }

    private fun observeConversation(conversationId: String) {
        viewModelScope.launch {
            conversationRepository.observeConversation(conversationId)
                .collect { conversation ->
                    if (conversation == null) {
                        _isConversationDeleted.value = true
                        return@collect
                    }

                    _typingUserIds.value = conversation.typingUserIds.filter { userId ->
                        userId != currentUserId
                    }
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
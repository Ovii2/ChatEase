package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.enums.ConversationType
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {

    suspend fun getUserConversations(userId: String): List<Conversation>

    suspend fun getConversation(conversationId: String): Conversation

    suspend fun getMessages(conversationId: String): List<Message>

    suspend fun createConversation(participantIds: List<String>, type: ConversationType): String

    suspend fun createGroupConversation(participantIds: List<String>): String

    suspend fun getExistingConversationId(participantIds: List<String>): String?

    fun observeUserConversations(userId: String): Flow<List<Conversation>>

    fun observeConversation(conversationId: String): Flow<Conversation?>

    suspend fun sendMessage(message: Message)

    fun observeMessages(conversationId: String): Flow<List<Message>>

    suspend fun markMessagesAsSeen(conversationId: String, currentUserId: String)

    suspend fun addReactionToMessage(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    )

    suspend fun deleteConversation(conversationId: String)

    suspend fun deleteIfEmptyConversation(conversationId: String)

    suspend fun updateTypingStatus(
        conversationId: String,
        userId: String,
        isTyping: Boolean
    )

    suspend fun deleteConversationWithMessages(conversationId: String)
}

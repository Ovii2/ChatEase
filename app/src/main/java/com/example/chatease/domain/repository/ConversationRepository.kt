package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Message

interface ConversationRepository {

    suspend fun getUserConversations(userId: String): List<Conversation>

    suspend fun getConversation(conversationId: String): Conversation

    suspend fun getMessages(conversationId: String): List<Message>

    suspend fun createConversation(participantIds: List<String>): String
}

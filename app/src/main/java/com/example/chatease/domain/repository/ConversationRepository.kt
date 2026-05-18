package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Conversation

interface ConversationRepository {

    suspend fun getUserConversations(
        userId: String
    ): List<Conversation>
}

package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Group

interface GroupRepository {

    suspend fun createGroup(
        conversationId: String,
        name: String,
        ownerId: String,
        memberIds: List<String>,
        imageUrl: String?
    ): String

    suspend fun getGroupByConversationId(conversationId: String): Group
}
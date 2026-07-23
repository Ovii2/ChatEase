package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Group

interface GroupRepository {

    suspend fun createGroup(
        conversationId: String,
        userIds: List<String>,
        adminIds: List<String>,
        name: String,
        ownerId: String,
        imageUrl: String?
    ): String

    suspend fun getGroupByConversationId(conversationId: String): Group

    suspend fun getGroups(currentUserId: String): List<Group>

    suspend fun addAdmin(conversationId: String, userId: String)
}
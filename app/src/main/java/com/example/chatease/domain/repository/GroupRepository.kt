package com.example.chatease.domain.repository

interface GroupRepository {

    suspend fun createGroup(
        name: String,
        ownerId: String,
        memberIds: List<String>,
        imageUrl: String?
    ): String
}
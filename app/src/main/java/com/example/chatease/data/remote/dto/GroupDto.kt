package com.example.chatease.data.remote.dto

data class GroupDto(
    val conversationId: String = "",
    val userIds: List<String> = emptyList(),
    val adminIds: List<String> = emptyList(),
    val visibleToUserIds: List<String> = emptyList(),
    val ownerId: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val removedAtByUserId: Map<String, Long> = emptyMap(),
    val categoryId: String = ""
)

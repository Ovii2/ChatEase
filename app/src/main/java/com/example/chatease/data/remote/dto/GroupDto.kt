package com.example.chatease.data.remote.dto

data class GroupDto(
    val conversationId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val imageUrl: String? = null
)

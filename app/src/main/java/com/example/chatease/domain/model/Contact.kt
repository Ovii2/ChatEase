package com.example.chatease.domain.model

data class Contact(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val createdAt: Long = 0L
)

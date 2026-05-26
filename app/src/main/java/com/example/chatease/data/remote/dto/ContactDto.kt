package com.example.chatease.data.remote.dto

data class ContactDto(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val createdAt: Long = 0L
)

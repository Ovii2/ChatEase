package com.example.chatease.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val isOnline: Boolean = false
)

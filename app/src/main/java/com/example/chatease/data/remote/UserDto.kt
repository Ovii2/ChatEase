package com.example.chatease.data.remote

data class UserDto(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val isOnline: Boolean = false
)

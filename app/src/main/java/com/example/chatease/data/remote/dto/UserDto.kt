package com.example.chatease.data.remote.dto

import com.example.chatease.domain.model.enums.UserPresenceStatus

data class UserDto(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val status: String = UserPresenceStatus.ONLINE.name,
    val blockedUserIds: List<String> = emptyList(),
    val fcmToken: String = ""
)
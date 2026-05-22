package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.UserPresenceStatus

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val status: UserPresenceStatus = UserPresenceStatus.OFFLINE
)

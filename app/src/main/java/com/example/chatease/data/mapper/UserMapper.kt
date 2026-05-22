package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.UserDto
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus

fun UserDto.toDomain(): User {
    return User(
        uid = uid,
        fullName = fullName,
        email = email,
        imageUrl = imageUrl,
        status = UserPresenceStatus.valueOf(status)
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        fullName = fullName,
        email = email,
        imageUrl = imageUrl,
        status = status.name
    )
}


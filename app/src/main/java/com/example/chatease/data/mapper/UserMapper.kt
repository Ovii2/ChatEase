package com.example.chatease.data.mapper

import com.example.chatease.data.remote.UserDto
import com.example.chatease.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        uid = uid,
        fullName = fullName,
        email = email,
        imageUrl = imageUrl,
        isOnline = isOnline
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        fullName = fullName,
        email = email,
        imageUrl = imageUrl,
        isOnline = isOnline
    )
}


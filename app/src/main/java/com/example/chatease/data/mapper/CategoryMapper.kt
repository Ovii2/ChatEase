package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.CategoryDto
import com.example.chatease.domain.model.Category

fun CategoryDto.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}

fun Category.toDto(): CategoryDto {
    return CategoryDto(
        id = id,
        name = name
    )
}

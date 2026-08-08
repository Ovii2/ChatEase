package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.FileAttachmentDto
import com.example.chatease.domain.model.FileAttachment

fun FileAttachment.toDto(): FileAttachmentDto {
    return FileAttachmentDto(
        id = id,
        name = name,
        size = size,
        url = url,
        mimeType = mimeType
    )
}

fun FileAttachmentDto.toDomain(): FileAttachment {
    return FileAttachment(
        id = id,
        name = name,
        size = size,
        url = url,
        mimeType = mimeType
    )
}
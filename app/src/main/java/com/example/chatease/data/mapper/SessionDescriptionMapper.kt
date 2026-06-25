package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.SessionDescriptionDto
import org.webrtc.SessionDescription


fun SessionDescriptionDto.toWebRtcSessionDescription(): SessionDescription {
    return SessionDescription(
        SessionDescription.Type.valueOf(type),
        description
    )
}

fun SessionDescription.toDto(): SessionDescriptionDto {
    return SessionDescriptionDto(
        type = type.name,
        description = description
    )
}

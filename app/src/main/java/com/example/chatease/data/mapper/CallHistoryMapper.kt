package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.CallHistoryDto
import com.example.chatease.domain.model.CallHistory

fun CallHistoryDto.toDomain(): CallHistory {
    return CallHistory(
        id = id,
        callerId = callerId,
        receiverId = receiverId,
        participantIds = participantIds,
        callType = callType,
        status = status,
        timestamp = timestamp,
        callDuration = callDuration
    )
}

fun CallHistory.toDto(): CallHistoryDto {
    return CallHistoryDto(
        id = id,
        callerId = callerId,
        receiverId = receiverId,
        participantIds = participantIds,
        callType = callType,
        status = status,
        timestamp = timestamp,
        callDuration = callDuration
    )
}

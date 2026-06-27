package com.example.chatease.data.remote.dto

import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType

data class CallHistoryDto(
    val id: String = "",
    val ownerId: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val participantIds: List<String> = emptyList(),
    val callType: CallType = CallType.AUDIO,
    val status: CallStatus = CallStatus.CALLING,
    val timestamp: Long = 0L,
    val callDuration: Long? = null,
    val seen: Boolean = false
)

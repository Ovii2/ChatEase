package com.example.chatease.data.remote.dto

import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.CallType

data class CallHistoryDto(
    val id: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val callType: CallType = CallType.AUDIO,
    val callDirection: CallDirection = CallDirection.OUTGOING,
    val timestamp: Long = 0L,
    val callDuration: Long? = null
)

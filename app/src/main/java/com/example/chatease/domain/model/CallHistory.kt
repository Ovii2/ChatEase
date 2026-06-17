package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.CallType

data class CallHistory(
    val id: String,
    val userId: String,
    val callType: CallType,
    val callDirection: CallDirection,
    val timestamp: Long,
    val callDuration: Long? = null
)

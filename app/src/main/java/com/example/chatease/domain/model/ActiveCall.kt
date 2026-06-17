package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType

data class ActiveCall(
    val callId: String,
    val callerId: String,
    val receiverId: String,
    val callType: CallType,
    val status: CallStatus
)

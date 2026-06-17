package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType

data class Call(
    val id: String,
    val callerId: String,
    val receiverId: String,
    val callType: CallType,
    val status: CallStatus
)

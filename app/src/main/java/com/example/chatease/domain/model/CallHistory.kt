package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType

data class CallHistory(
    val id: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val participantIds: List<String> = emptyList(),
    val callType: CallType = CallType.AUDIO,
    val status: CallStatus = CallStatus.CALLING,
    val timestamp: Long = 0L,
    val callDuration: Long? = null
)

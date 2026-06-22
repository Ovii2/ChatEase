package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType

data class Call(
    val id: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val callType: CallType = CallType.AUDIO,
    val status: CallStatus = CallStatus.CALLING,
    val conversationId: String = ""
)

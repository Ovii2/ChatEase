package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.SessionDescriptionType

data class SessionDescription(
    val type: SessionDescriptionType,
    val description: String
)

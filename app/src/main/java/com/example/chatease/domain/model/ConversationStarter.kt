package com.example.chatease.domain.model

import androidx.annotation.StringRes

data class ConversationStarter(
    @StringRes val text: Int,
    val emoji: String? = null
)

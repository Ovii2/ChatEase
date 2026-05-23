package com.example.chatease.presentation.ui.model

class CooldownUiModel(
    val userId: String,
    val expiresAt: Long
) {
    val remainingCooldownLeft: Long
        get() = expiresAt - System.currentTimeMillis()
}

package com.example.chatease.domain.model

import androidx.annotation.StringRes

data class ProfileStat(
    val value: String,
    @StringRes val label: Int
)

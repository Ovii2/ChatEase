package com.example.chatease.presentation.ui.model

import com.example.chatease.domain.model.User

data class ConversationUiModel(
    val conversationId: String,
    val title: String,
    val imageUrl: String?,
    val participants: List<User>,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int,
    val isGroup: Boolean,
    val isBlockedByOtherUser: Boolean = false,
    val isCurrentUserGroupMember: Boolean,
    val categoryId: String? = null
)
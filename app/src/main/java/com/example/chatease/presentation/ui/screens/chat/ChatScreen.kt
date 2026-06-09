package com.example.chatease.presentation.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = hiltViewModel(),
    conversationId: String,
    onBackClick: () -> Unit,
    onNavigateToChatInfo: (String) -> Unit,
) {
    val user by chatViewModel.user.collectAsState()
    val messages by chatViewModel.messages.collectAsState()

    LaunchedEffect(conversationId) {
        chatViewModel.loadConversation(conversationId)
    }

    RightPane(
        user = user,
        messages = messages,
        currentUserId = chatViewModel.currentUserId,
        onBackClick = onBackClick,
        onSendMessageClick = { chatViewModel.sendMessage(conversationId, it) },
        firstUnreadMessageId = chatViewModel.firstUnreadMessageId,
        onMessagesVisible = { chatViewModel.markMessagesAsSeen(conversationId) },
        onReactionClick = { messageId, reaction ->
            chatViewModel.addReactionToMessage(
                conversationId = conversationId,
                messageId = messageId,
                reaction = reaction
            )
        },
        onNavigateToChatInfo = { onNavigateToChatInfo(conversationId) }
    )
}
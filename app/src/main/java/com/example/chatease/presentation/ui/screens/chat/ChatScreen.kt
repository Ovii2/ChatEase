package com.example.chatease.presentation.ui.screens.chat

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onNavigateToHomeScreen: () -> Unit
) {
    val user by chatViewModel.user.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    val isConversationDeleted by chatViewModel.isConversationDeleted.collectAsState()

    LaunchedEffect(conversationId) {
        chatViewModel.loadConversation(conversationId)
    }

    LaunchedEffect(isConversationDeleted) {
        if (isConversationDeleted) {
            onNavigateToHomeScreen()
        }
    }

    BackHandler {
        chatViewModel.deleteConversationIfEmpty(conversationId)
        onBackClick()
    }

    RightPane(
        user = user,
        messages = messages,
        currentUserId = chatViewModel.currentUserId,
        onBackClick = {
            chatViewModel.deleteConversationIfEmpty(conversationId)
            onBackClick()
        },
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
        onNavigateToChatInfo = { onNavigateToChatInfo(conversationId) },
        isPeekEnabled = isPeekEnabled,
        onPeekClick = { isPeekEnabled = !isPeekEnabled }
    )
}
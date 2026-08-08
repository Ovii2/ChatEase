package com.example.chatease.presentation.ui.screens.group_chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.presentation.ui.screens.shared.chat.ReactionsDetailsBottomSheet
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.state.GroupChatUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.ChatViewModel
import com.example.chatease.presentation.ui.viewmodel.GroupChatViewModel
import com.example.chatease.utils.openFile

@Composable
fun GroupChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    conversationId: String,
    groupChatViewModel: GroupChatViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    onNavigateToGroupChatInfo: (String) -> Unit
) {
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    val isBlockedByOtherUser = false
    val uiState by groupChatViewModel.uiState.collectAsState()
    val currentUserId = chatViewModel.currentUserId
    val context = LocalContext.current
    val isUserGroupMember =
        (uiState as? GroupChatUiState.Success)
            ?.group
            ?.userIds
            ?.contains(currentUserId)
            ?: false

    LaunchedEffect(conversationId) {
        groupChatViewModel.loadGroupConversation(conversationId)
        chatViewModel.loadConversation(conversationId)
    }

    BackHandler {
        if (isUserGroupMember) {
            chatViewModel.markMessagesAsSeen(conversationId)
        }

        chatViewModel.updateTypingStatus(conversationId, false)
        chatViewModel.deleteConversationIfEmpty(conversationId)
        onBackClick()
    }

    val firstUnreadMessageId = chatViewModel.firstUnreadMessageId ?: ""
    val typingUserIds by chatViewModel.typingUserIds.collectAsState()
    var selectedReactionsMessageId by rememberSaveable { mutableStateOf<String?>(null) }

    when (val state = uiState) {
        is GroupChatUiState.Success -> {
            val selectedMessage = state.messages.firstOrNull { message ->
                message.messageId == selectedReactionsMessageId
            }

            Scaffold(
                modifier = modifier.padding(vertical = 8.dp),
            ) { paddingValues ->
                RightPane(
                    messages = state.messages,
                    currentUserId = currentUserId,
                    onBackClick = {
                        if (isUserGroupMember) {
                            chatViewModel.markMessagesAsSeen(conversationId)
                        }

                        chatViewModel.updateTypingStatus(conversationId, false)
                        chatViewModel.deleteConversationIfEmpty(conversationId)
                        onBackClick()
                    },
                    onSendMessageClick = { text, repliedMessage ->
                        chatViewModel.sendMessage(
                            conversationId = conversationId,
                            text = text,
                            repliedMessage = repliedMessage
                        )
                    },
                    firstUnreadMessageId = firstUnreadMessageId,
                    onMessagesVisible = { chatViewModel.markMessagesAsSeen(conversationId) },
                    onReactionClick = { messageId, reaction ->
                        chatViewModel.addReactionToMessage(
                            conversationId = conversationId,
                            messageId = messageId,
                            reaction = reaction
                        )
                    },
                    onNavigateToChatInfo = {},
                    isPeekEnabled = isPeekEnabled,
                    onPeekClick = { isPeekEnabled = !isPeekEnabled },
                    typingUserIds = typingUserIds,
                    updateTypingStatus = { text ->
                        chatViewModel.updateTypingStatus(
                            conversationId = conversationId,
                            isTyping = text.isNotBlank()
                        )
                    },
                    isBlockedByOtherUser = isBlockedByOtherUser,
                    onStartAudioCall = {},
                    chatPaneUiState = ChatPaneUiState.GroupChat(
                        group = state.group,
                        members = state.members
                    ),
                    onNavigateToGroupChatInfo = onNavigateToGroupChatInfo,
                    onShowUsersReactionsClick = { messageId ->
                        selectedReactionsMessageId = messageId
                    },
                    onSendFile = { uri ->
                        chatViewModel.sendFile(
                            conversationId = conversationId,
                            fileUri = uri,
                            currentUserId = currentUserId
                        )
                    },
                    onFileClick = { message ->
                        val fileAttachment = message.fileAttachment ?: return@RightPane

                        chatViewModel.openFile(
                            messageId = message.messageId,
                            fileUrl = fileAttachment.url,
                            fileName = fileAttachment.name,
                            onFileReady = { uri ->
                                openFile(
                                    context = context,
                                    uri = uri,
                                    mimeType = fileAttachment.mimeType
                                )
                            }
                        )
                    }
                )
                selectedMessage?.let { message ->
                    ReactionsDetailsBottomSheet(
                        onDismissRequest = { selectedReactionsMessageId = null },
                        users = state.members.filter { user ->
                            user.uid in message.reactions.keys
                        },
                        reactions = message.reactions
                    )
                }
            }
        }

        is GroupChatUiState.Error -> {}
        GroupChatUiState.Loading -> {
            CommonCircularLoader()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatScreen(
                    onBackClick = {},
                    conversationId = "1",
                    onNavigateToGroupChatInfo = {},
                )
            }
        }
    }
}

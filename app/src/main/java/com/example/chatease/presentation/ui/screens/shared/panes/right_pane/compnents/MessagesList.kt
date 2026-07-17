package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.group_chat.components.SeenByRow
import com.example.chatease.presentation.ui.screens.shared.chat.CommonChip
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.isSameDay
import com.example.chatease.utils.toChatDateLabel

@Composable
fun MessagesList(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    currentUserId: String,
    listState: LazyListState,
    firstUnreadMessageId: String?,
    onReactionClick: (String, String) -> Unit,
    isBlockedByOtherUser: Boolean,
    chatPaneUiState: ChatPaneUiState
) {
    var selectedReactionMessageId by rememberSaveable { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            selectedReactionMessageId = null
            focusManager.clearFocus()
        }
    ) {
        val reversedMessages = messages.reversed()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(reversedMessages) { index, message ->
                val previousVisibleMessage = reversedMessages.getOrNull(index + 1)
                val nextVisibleMessage = reversedMessages.getOrNull(index - 1)

                val isFirstInGroup = previousVisibleMessage?.senderId != message.senderId
                val isLastInGroup = nextVisibleMessage?.senderId != message.senderId
                val isMiddleInGroup = !isFirstInGroup && !isLastInGroup

                val isSentByCurrentUser = message.senderId == currentUserId

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (message.reactions.isNotEmpty()) 18.dp else 2.dp),
                    horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
                ) {
                    when (chatPaneUiState) {
                        is ChatPaneUiState.DirectChat -> {
                            DirectMessageListItem(
                                isSentByCurrentUser = isSentByCurrentUser,
                                user = chatPaneUiState.user,
                                isBlockedByOtherUser = isBlockedByOtherUser,
                                message = message,
                                currentUserId = currentUserId,
                                selectedReactionMessageId = selectedReactionMessageId ?: "",
                                isFirstInGroup = isFirstInGroup,
                                isMiddleInGroup = isMiddleInGroup,
                                isLastInGroup = isLastInGroup,
                                onLongClick = { selectedReactionMessageId = message.messageId },
                                onDismissReactions = { selectedReactionMessageId = null },
                                onReactionClick = { messageId, reaction ->
                                    onReactionClick(messageId, reaction)
                                    selectedReactionMessageId = null
                                }
                            )
                        }

                        is ChatPaneUiState.GroupChat -> {
                            val seenUsers = chatPaneUiState.members.filter { member ->
                                member.uid != currentUserId && member.uid in message.seenBy
                            }

                            val sender = chatPaneUiState.members.firstOrNull { user ->
                                user.uid == message.senderId
                            } ?: return@itemsIndexed

                            GroupMessageListItem(
                                user = sender,
                                message = message,
                                currentUserId = currentUserId,
                                isSentByCurrentUser = isSentByCurrentUser,
                                selectedReactionMessageId = selectedReactionMessageId ?: "",
                                isFirstInGroup = isFirstInGroup,
                                isMiddleInGroup = isMiddleInGroup,
                                isLastInGroup = isLastInGroup,
                                onLongClick = { selectedReactionMessageId = message.messageId },
                                onDismissReactions = { selectedReactionMessageId = null },
                                onReactionClick = { messageId, reaction ->
                                    onReactionClick(messageId, reaction)
                                    selectedReactionMessageId = null
                                },
                                seenUsers = seenUsers
                            )
                        }
                    }

                }
                if (firstUnreadMessageId != null && message.messageId == firstUnreadMessageId) {
                    UnreadMessagesDivider()
                }
                if (previousVisibleMessage == null || !isSameDay(
                        message.timeStamp,
                        previousVisibleMessage.timeStamp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CommonChip(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = message.timeStamp.toChatDateLabel(context),
                            selected = false,
                            enabled = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DirectMessageListItem(
    modifier: Modifier = Modifier,
    isSentByCurrentUser: Boolean,
    user: User,
    isBlockedByOtherUser: Boolean,
    message: Message,
    currentUserId: String,
    selectedReactionMessageId: String,
    isFirstInGroup: Boolean,
    isMiddleInGroup: Boolean,
    isLastInGroup: Boolean,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (!isSentByCurrentUser) {
            UserAvatar(
                user = user,
                avatarSize = 50.dp,
                statusBubbleSize = 14.dp,
                initialsFontSize = 20.sp,
                showStatus = !isBlockedByOtherUser
            )
        } else Unit
        ChatBubble(
            message = message,
            isSentByCurrentUser = isSentByCurrentUser,
            currentUserId = currentUserId,
            showReactions = message.messageId == selectedReactionMessageId,
            isFirstInGroup = isFirstInGroup,
            isMiddleInGroup = isMiddleInGroup,
            isLastInGroup = isLastInGroup,
            onLongClick = onLongClick,
            onDismissReactions = onDismissReactions,
            onReactionClick = onReactionClick
        )
    }
}

@Composable
fun GroupMessageListItem(
    modifier: Modifier = Modifier,
    user: User,
    message: Message,
    isSentByCurrentUser: Boolean,
    currentUserId: String,
    selectedReactionMessageId: String,
    isFirstInGroup: Boolean,
    isMiddleInGroup: Boolean,
    isLastInGroup: Boolean,
    onLongClick: () -> Unit,
    onDismissReactions: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    seenUsers: List<User>
) {
    val fullName = user.fullName
    val name = fullName.split(" ")[0]
    val lastname = fullName.split(" ")[1]
    val lastnameInitial = lastname.first()
    val arrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isSentByCurrentUser) {
            UserAvatar(
                user = user,
                avatarSize = 50.dp,
                statusBubbleSize = 14.dp,
                initialsFontSize = 20.sp,
            )
        }
        Column {
            if (!isSentByCurrentUser) {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "$name $lastnameInitial.",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            ChatBubble(
                message = message,
                isSentByCurrentUser = isSentByCurrentUser,
                currentUserId = currentUserId,
                showReactions = message.messageId == selectedReactionMessageId,
                isFirstInGroup = isFirstInGroup,
                isMiddleInGroup = isMiddleInGroup,
                isLastInGroup = isLastInGroup,
                onLongClick = onLongClick,
                onDismissReactions = onDismissReactions,
                onReactionClick = onReactionClick,
                conversationType = ConversationType.GROUP
            )
            if (seenUsers.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, top = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SeenByRow(
                        users = seenUsers
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MessagesListPreview() {
    val messages = List(5) {
        Message(
            messageId = it.toString(),
            conversationId = "conversation_1",
            senderId = listOf("user_1", "user_2").random(),
            text = LoremIpsum((5..20).random()).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1"),
            reactions = mapOf(
                "user_1" to "\uD83E\uDD70",
                "user_2" to "\uD83E\uDD70"
            )
        )
    }

    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val directChatPaneUiState = ChatPaneUiState.DirectChat(
        user = user
    )

    ChatEaseTheme {
        Column(modifier = Modifier.systemBarsPadding()) {
            MessagesList(
                messages = messages,
                currentUserId = "user_2",
                listState = rememberLazyListState(),
                firstUnreadMessageId = "1",
                onReactionClick = { _, _ -> },
                isBlockedByOtherUser = true,
                chatPaneUiState = directChatPaneUiState,
            )
        }
    }
}


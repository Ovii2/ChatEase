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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.MessageType
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
    chatPaneUiState: ChatPaneUiState,
    onShowUsersReactionsClick: (String) -> Unit,
    onLongClick: (Message) -> Unit,
    selectedReactionMessageId: String?,
    onSelectedReactionMessageIdChange: (String?) -> Unit,
    onDismissMessageActions: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val reversedMessages = messages.reversed()

    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onSelectedReactionMessageIdChange(null)
            onDismissMessageActions()
            focusManager.clearFocus()
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(reversedMessages) { index, message ->
                val previousVisibleMessage = reversedMessages.getOrNull(index + 1)
                val nextVisibleMessage = reversedMessages.getOrNull(index - 1)

                val isFirstInGroup = previousVisibleMessage?.senderId != message.senderId
                val isLastInGroup = nextVisibleMessage?.senderId != message.senderId
                val isMiddleInGroup = !isFirstInGroup && !isLastInGroup
                val isSentByCurrentUser = message.senderId == currentUserId
                val messageBottomPadding = if (message.reactions.isNotEmpty()) 18.dp else 2.dp

                when (chatPaneUiState) {
                    is ChatPaneUiState.DirectChat -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = messageBottomPadding),
                            horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
                        ) {
                            DirectMessageListItem(
                                isSentByCurrentUser = isSentByCurrentUser,
                                user = chatPaneUiState.user,
                                isBlockedByOtherUser = isBlockedByOtherUser,
                                message = message,
                                currentUserId = currentUserId,
                                selectedReactionMessageId = selectedReactionMessageId.orEmpty(),
                                isFirstInGroup = isFirstInGroup,
                                isMiddleInGroup = isMiddleInGroup,
                                isLastInGroup = isLastInGroup,
                                onLongClick = {
                                    onSelectedReactionMessageIdChange(message.messageId)
                                    onLongClick(message)
                                },
                                onDismissReactions = {
                                    onSelectedReactionMessageIdChange(null)
                                },
                                onReactionClick = { messageId, reaction ->
                                    onReactionClick(messageId, reaction)
                                    onSelectedReactionMessageIdChange(null)
                                },
                                onShowUsersReactionsClick = onShowUsersReactionsClick
                            )
                        }
                    }

                    is ChatPaneUiState.GroupChat -> {
                        val sender = chatPaneUiState.members.firstOrNull { user ->
                            user.uid == message.senderId
                        } ?: return@itemsIndexed

                        val seenUsers = chatPaneUiState.members.filter { member ->
                            member.uid != currentUserId && member.uid in message.seenBy
                        }

                        val showSeenBy = index == 0 && seenUsers.isNotEmpty()
                        val isUserMemberOfGroup = currentUserId in chatPaneUiState.group.userIds

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = messageBottomPadding),
                                horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
                            ) {
                                GroupMessageListItem(
                                    user = sender,
                                    message = message,
                                    currentUserId = currentUserId,
                                    isSentByCurrentUser = isSentByCurrentUser,
                                    selectedReactionMessageId = selectedReactionMessageId.orEmpty(),
                                    isFirstInGroup = isFirstInGroup,
                                    isMiddleInGroup = isMiddleInGroup,
                                    isLastInGroup = isLastInGroup,
                                    onLongClick = {
                                        onSelectedReactionMessageIdChange(message.messageId)
                                        onLongClick(message)
                                    },
                                    onDismissReactions = {
                                        onSelectedReactionMessageIdChange(null)
                                        onDismissMessageActions()
                                    },
                                    onReactionClick = { messageId, reaction ->
                                        onReactionClick(messageId, reaction)
                                        onSelectedReactionMessageIdChange(null)
                                    },
                                    onShowUsersReactionsClick = onShowUsersReactionsClick,
                                    isUserMemberOfGroup = isUserMemberOfGroup,
                                    members = chatPaneUiState.members,
                                )
                            }

                            if (showSeenBy) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 3.dp, end = 2.dp, bottom = 2.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    SeenByRow(users = seenUsers)
                                }
                            }
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
    onReactionClick: (String, String) -> Unit,
    onShowUsersReactionsClick: (String) -> Unit
) {
    val otherUserFirstName = user.fullName
        .trim()
        .split(" ")
        .firstOrNull()
        .orEmpty()

    val messageSenderName =
        if (isSentByCurrentUser) stringResource(R.string.you) else otherUserFirstName

    val repliedMessageSenderName =
        if (message.replyMessage?.senderId == currentUserId) stringResource(R.string.you_lowercase) else otherUserFirstName

    Row(
        modifier = modifier,
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
        }
        when (message.messageType) {
            MessageType.TEXT -> {
                if (message.replyMessage != null) {
                    ReplyChatBubble(
                        message = message,
                        isSentByCurrentUser = isSentByCurrentUser,
                        currentUserId = currentUserId,
                        showReactions = message.messageId == selectedReactionMessageId,
                        onLongClick = onLongClick,
                        onDismissReactions = onDismissReactions,
                        onReactionClick = onReactionClick,
                        onShowUsersReactionsClick = onShowUsersReactionsClick,
                        isBlockedByOtherUser = isBlockedByOtherUser,
                        isUserMemberOfGroup = true,
                        messageSenderName = messageSenderName,
                        repliedMessageSenderName = repliedMessageSenderName
                    )
                } else {
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
                        onShowUsersReactionsClick = onShowUsersReactionsClick,
                        isBlockedByOtherUser = isBlockedByOtherUser,
                        isUserMemberOfGroup = true,
                    )
                }
            }

            MessageType.FILE -> {
                FileChatBubble(
                    filename = message.fileAttachment?.name ?: "",
                    onForwardClick = {},
                    onLongClick = onLongClick,
                    message = message,
                    isSentByCurrentUser = isSentByCurrentUser,
                    showReactions = message.messageId == selectedReactionMessageId,
                    onDismissReactions = onDismissReactions,
                    onReactionClick = onReactionClick,
                    onShowUsersReactionsClick = onShowUsersReactionsClick,
                    isBlockedByOtherUser = isBlockedByOtherUser,
                    isUserMemberOfGroup = true,
                )
            }

            MessageType.IMAGE -> {}
            MessageType.GIF -> {}
            MessageType.AUDIO -> {}
            MessageType.VIDEO -> {}
        }
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
    onShowUsersReactionsClick: (String) -> Unit,
    isUserMemberOfGroup: Boolean,
    members: List<User>
) {
    val nameParts = user.fullName
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    val firstName = nameParts.firstOrNull().orEmpty()
    val lastNameInitial = nameParts
        .getOrNull(1)
        ?.firstOrNull()

    val displayedName = if (lastNameInitial != null) "$firstName $lastNameInitial." else firstName
    val messageSenderName = if (isSentByCurrentUser) stringResource(R.string.you) else firstName

    val repliedUser = members.firstOrNull { member ->
        member.uid == message.replyMessage?.senderId
    }

    val repliedMessageSenderName =
        if (message.replyMessage?.senderId == currentUserId) {
            stringResource(R.string.you_lowercase)
        } else {
            repliedUser?.fullName
                ?.trim()
                ?.split(" ")
                ?.firstOrNull()
                .orEmpty()
        }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (!isSentByCurrentUser) {
            UserAvatar(
                user = user,
                avatarSize = 50.dp,
                statusBubbleSize = 14.dp,
                initialsFontSize = 20.sp
            )
        }

        Column {
            if (!isSentByCurrentUser && message.replyMessage == null) {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = displayedName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            when (message.messageType) {
                MessageType.TEXT -> {
                    if (message.replyMessage != null) {
                        ReplyChatBubble(
                            message = message,
                            isSentByCurrentUser = isSentByCurrentUser,
                            currentUserId = currentUserId,
                            showReactions = message.messageId == selectedReactionMessageId,
                            onLongClick = onLongClick,
                            onDismissReactions = onDismissReactions,
                            onReactionClick = onReactionClick,
                            conversationType = ConversationType.GROUP,
                            onShowUsersReactionsClick = onShowUsersReactionsClick,
                            isBlockedByOtherUser = false,
                            isUserMemberOfGroup = isUserMemberOfGroup,
                            messageSenderName = messageSenderName,
                            repliedMessageSenderName = repliedMessageSenderName
                        )
                    } else {
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
                            conversationType = ConversationType.GROUP,
                            onShowUsersReactionsClick = onShowUsersReactionsClick,
                            isBlockedByOtherUser = false,
                            isUserMemberOfGroup = isUserMemberOfGroup,
                        )
                    }
                }

                MessageType.FILE -> {
                    FileChatBubble(
                        filename = message.fileAttachment?.name ?: "",
                        onForwardClick = {},
                        onLongClick = onLongClick,
                        message = message,
                        isSentByCurrentUser = isSentByCurrentUser,
                        showReactions = message.messageId == selectedReactionMessageId,
                        onDismissReactions = onDismissReactions,
                        onReactionClick = onReactionClick,
                        onShowUsersReactionsClick = onShowUsersReactionsClick,
                        isBlockedByOtherUser = false,
                        isUserMemberOfGroup = true,
                    )
                }

                MessageType.IMAGE -> {}
                MessageType.GIF -> {}
                MessageType.AUDIO -> {}
                MessageType.VIDEO -> {}
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MessagesListPreview() {
//    val messages = List(5) {
//        Message(
//            messageId = it.toString(),
//            conversationId = "1",
//            senderId = listOf("1", "2").random(),
//            text = LoremIpsum((5..20).random()).values.first(),
//            timeStamp = System.currentTimeMillis(),
//            seenBy = listOf("1", "2", "3"),
//            reactions = mapOf(
//                "1" to "\uD83E\uDD70",
//                "2" to "\uD83E\uDD70"
//            )
//        )
//    }
    val messages = List(2) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = "1",
            timeStamp = System.currentTimeMillis(),
            messageType = MessageType.FILE,
            fileAttachment = FileAttachment(
                name = "Project_document_final_version.pdf",
                size = 4_194_304L,
                url = "test",
                mimeType = "application/pdf"
            ),
            reactions = mapOf(
                "1" to "\uD83E\uDD70",
                "2" to "\uD83E\uDD70"
            )
        )
    }


    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val groupChatPaneUiState = ChatPaneUiState.GroupChat(
        group = Group(
            conversationId = "1",
            userIds = listOf("1", "2", "3"),
            adminIds = listOf("1"),
            ownerId = "1",
            name = "Group Chat",
            imageUrl = null
        ),
        members = List(5) {
            User(
                uid = it.toString(),
                fullName = "Test Test",
                email = "test@email.com",
                imageUrl = null,
                status = UserPresenceStatus.ONLINE
            )
        }
    )

    val directChatPaneUiState = ChatPaneUiState.DirectChat(
        user = user
    )

    ChatEaseTheme {
        Column(modifier = Modifier.systemBarsPadding()) {
            MessagesList(
                messages = messages,
                currentUserId = "1",
                listState = rememberLazyListState(),
                firstUnreadMessageId = "1",
                onReactionClick = { _, _ -> },
                isBlockedByOtherUser = true,
                chatPaneUiState = directChatPaneUiState,
                onShowUsersReactionsClick = {},
                onLongClick = {},
                selectedReactionMessageId = "1",
                onSelectedReactionMessageIdChange = {},
                onDismissMessageActions = {},
            )
        }
    }
}
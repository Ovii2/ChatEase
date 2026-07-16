package com.example.chatease.presentation.ui.screens.group_chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.CommonChip
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.ChatBubble
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.UnreadMessagesDivider
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.isSameDay
import com.example.chatease.utils.toChatDateLabel

@Composable
fun GroupMessageList(
    modifier: Modifier = Modifier,
    user: User,
    messages: List<Message>,
    currentUserId: String,
    listState: LazyListState,
    groupMembers: List<User>,
    firstUnreadMessageId: String?
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(messages) { index, message ->
            val previousVisibleMessage = messages.getOrNull(index + 1)
            val seenUsers = groupMembers.filter { member ->
                member.uid != currentUserId && member.uid in message.seenBy
            }
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
            val isSentByCurrentUser = message.senderId == currentUserId
            GroupMessageListItem(
                user = user,
                message = message,
                isSentByCurrentUser = isSentByCurrentUser
            )
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

@Composable
fun GroupMessageListItem(
    modifier: Modifier = Modifier,
    user: User,
    message: Message,
    isSentByCurrentUser: Boolean
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
                currentUserId = "1",
                showReactions = false,
                isFirstInGroup = false,
                isMiddleInGroup = false,
                isLastInGroup = false,
                onLongClick = {},
                onDismissReactions = {},
                onReactionClick = { _, _ -> },
                conversationType = ConversationType.GROUP
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "List item")
@Composable
private fun GroupMessageListItemPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    val message = Message(
        messageId = "1",
        conversationId = "1",
        senderId = "1",
        text = LoremIpsum(20).values.first(),
        timeStamp = System.currentTimeMillis(),
        seenBy = listOf(""),
        reactions = emptyMap(),
        messageType = MessageType.TEXT
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupMessageListItem(
                    user = user,
                    message = message,
                    isSentByCurrentUser = false
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "List")
@Composable
private fun GroupMessageListPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    fun generateRandomUserIds(count: Int): List<String> {
        return (1..10)
            .shuffled()
            .take(count)
            .map { "user_$it" }
    }

    val message = List(3) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = listOf("user_1", "user_2").random(),
            text = LoremIpsum(1).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = generateRandomUserIds(10),
            reactions = emptyMap(),
            messageType = MessageType.TEXT
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupMessageList(
                    user = user,
                    messages = message,
                    currentUserId = "user_1",
                    listState = rememberLazyListState(),
                    groupMembers = List(10) {
                        User(
                            uid = "user_$it",
                            fullName = "Test Test",
                            email = "",
                            imageUrl = null,
                            status = UserPresenceStatus.ONLINE,
                            blockedUserIds = emptyList(),

                            )
                    },
                    firstUnreadMessageId = "1"
                )
            }
        }
    }
}

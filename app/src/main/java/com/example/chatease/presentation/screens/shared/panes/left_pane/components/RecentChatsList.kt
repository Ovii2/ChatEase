package com.example.chatease.presentation.screens.shared.panes.left_pane.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserStatus
import com.example.chatease.presentation.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toChatTimeStamp

@Composable
fun RecentChatsList(
    modifier: Modifier = Modifier,
    conversations: List<Conversation>,
    onConversationClick: (String) -> Unit,
    onClickToSeeAll: () -> Unit
) {
    Column() {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.recent_chats),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            if (conversations.isNotEmpty()) {
                Text(
                    modifier = Modifier.clickable { onClickToSeeAll() },
                    text = stringResource(R.string.see_all),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (conversations.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_chats),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(R.string.start_chatting),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(conversations) { index, conversation ->
                    RecentChatListItem(
                        conversation = conversation,
                        onNavigateToChatDetails = onConversationClick,
                        showDivider = index != conversations.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
fun RecentChatListItem(
    modifier: Modifier = Modifier,
    conversation: Conversation,
    onNavigateToChatDetails: (String) -> Unit,
    showDivider: Boolean
) {
    val user = conversation.participants.firstOrNull() ?: return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onNavigateToChatDetails(conversation.id) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                user = user
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.widthIn(max = 200.dp)
            ) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(start = 1.5.dp),
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = conversation.timestamp.toChatTimeStamp(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString(),
                        color = MaterialTheme.colorScheme.surface,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                Box(modifier = Modifier.size(25.dp)) { }
            }
        }
    }
    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.padding(
                start = 88.dp,
                end = 16.dp,
                top = 8.dp
            ),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RecentChatsListPreview() {
    val user = User(
        uid = "1",
        fullName = "Test test",
        email = "test@email.com",
        imageUrl = null,
        status = UserStatus.AWAY
    )

    val conversation = Conversation(
        id = "1",
        participants = listOf(user),
        lastMessage = "Hey! Are we still for lunch?",
        timestamp = System.currentTimeMillis(),
        unreadCount = 2
    )
    ChatEaseTheme() {
        Column(modifier = Modifier.systemBarsPadding()) {
            RecentChatsList(
                conversations = listOf(conversation, conversation, conversation, conversation),
                onConversationClick = {},
                onClickToSeeAll = {},
            )
        }
    }
}

//@Preview(
//    showBackground = true, showSystemUi = true,
//    uiMode = Configuration.UI_MODE_TYPE_NORMAL
//)
//@Composable
//fun RecentChatListItemPreview(modifier: Modifier = Modifier) {
//    val user = User(
//        uid = "1",
//        fullName = "Test test",
//        email = "test@email.com",
//        imageUrl = null,
//        status = UserStatus.ONLINE
//    )
//
//    val conversation = Conversation(
//        id = "1",
//        participants = listOf(user),
//        lastMessage = "Hey! Are we still for lunch?",
//        timestamp = System.currentTimeMillis(),
//        unreadCount = 9
//    )
//    ChatEaseTheme() {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            RecentChatListItem(
//                conversation = conversation,
//                onNavigateToChatDetails = {},
//                showDivider = true,
//            )
//        }
//    }
//}
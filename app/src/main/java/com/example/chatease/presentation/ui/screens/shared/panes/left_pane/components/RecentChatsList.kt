package com.example.chatease.presentation.ui.screens.shared.panes.left_pane.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.ConversationUiModel
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.screens.shared.group.GroupAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toChatTimeStamp

@Composable
fun RecentChatsList(
    modifier: Modifier = Modifier,
    conversations: List<ConversationUiModel>,
    onConversationClick: (String) -> Unit,
    onClickToSeeAll: () -> Unit,
    group: Group
) {
    val cornerShape = RoundedCornerShape(24.dp)

    Column {
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
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(conversations) { conversation ->
                    RecentChatListItem(
                        conversation = conversation,
                        onNavigateToChatDetails = onConversationClick,
                        cornerShape = cornerShape,
                        group = group
                    )
                }
            }
        }
    }
}

@Composable
fun RecentChatListItem(
    modifier: Modifier = Modifier,
    conversation: ConversationUiModel,
    onNavigateToChatDetails: (String) -> Unit,
    cornerShape: RoundedCornerShape,
    group: Group
) {
    val user = conversation.participants.firstOrNull() ?: return
    val backgroundColor =
        if (conversation.unreadCount > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else
            MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .clip(cornerShape)
            .background(color = backgroundColor)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onNavigateToChatDetails(
                        conversation.conversationId
                    )
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (conversation.isGroup) {
                    GroupAvatar(
                        group = group,
                        imageSize = 60.dp
                    )
                } else {
                    UserAvatar(
                        user = user,
                        showStatus = !conversation.isBlockedByOtherUser
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.widthIn(max = 200.dp)
                ) {
                    Text(
                        text = conversation.title,
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
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
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
        status = UserPresenceStatus.AWAY
    )

    val group = Group(
        conversationId = "1",
        ownerId = "1",
        name = "Group conversation",
        imageUrl = null
    )
    ChatEaseTheme {
        Column(modifier = Modifier.systemBarsPadding()) {
            RecentChatsList(
                conversations = List(4) {
                    ConversationUiModel(
                        conversationId = "1",
                        title = "Test Test",
                        imageUrl = null,
                        participants = listOf(user),
                        lastMessage = "Test message",
                        timestamp = System.currentTimeMillis(),
                        unreadCount = 1,
                        isGroup = it % 2 == 0
                    )
                },
                onConversationClick = {},
                onClickToSeeAll = {},
                group = group
            )
        }
    }
}
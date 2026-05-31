package com.example.chatease.presentation.ui.screens.shared.panes.right_pane

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.ConversationStarterRow
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessageInputBar
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.MessagesList
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents.RightPaneTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun RightPane(
    modifier: Modifier = Modifier,
    user: User,
    messages: List<Message>,
    currentUserId: String,
    onBackClick: () -> Unit,
    onSendMessageClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var messageText by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus()
        }
    ) {
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RightPaneTopBar(
                user = user,
                onBackClick = onBackClick
            )
            MessagesList(
                modifier = Modifier.weight(1f),
                messages = messages,
                currentUserId = currentUserId,
                user = user
            )
            if (messages.isEmpty()) {
                ConversationStarterRow(
                    onStarterClick = { messageText = it }
                )
            }
            MessageInputBar(
                onEmojiClick = {},
                onMicrophoneClick = {},
                onSendMessageClick = {
                    onSendMessageClick(it)
                    messageText = ""
                },
                messageText = messageText,
                onMessageTextChange = { messageText = it },
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RightPanePreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val messages = listOf(
        Message(
            messageId = "1",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Hey! Are we still for lunch?",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_2")
        ),
        Message(
            messageId = "2",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "I was thinking maybe we could also stop by that new café near the park afterwards." +
                    " I heard they have really good desserts and coffee there 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "3",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Yes, definitely! GG 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "4",
            conversationId = "conversation_1",
            senderId = "user_1",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "5",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        ),
        Message(
            messageId = "6",
            conversationId = "conversation_1",
            senderId = "user_2",
            text = "Yes, definitely! 😄",
            timeStamp = System.currentTimeMillis(),
            seenBy = listOf("user_1")
        )
    )
    ChatEaseTheme() {
        Column() {
            RightPane(
                user = user,
                messages = messages,
                currentUserId = "user_2",
                onBackClick = {},
                onSendMessageClick = {},
            )
        }
    }
}
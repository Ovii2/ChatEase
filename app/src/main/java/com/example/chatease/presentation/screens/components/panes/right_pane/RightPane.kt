package com.example.chatease.presentation.screens.components.panes.right_pane

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserStatus
import com.example.chatease.presentation.screens.components.panes.right_pane.compnents.MessageInputBar
import com.example.chatease.presentation.screens.components.panes.right_pane.compnents.MessagesList
import com.example.chatease.presentation.screens.components.panes.right_pane.compnents.RightPaneTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun RightPane(
    modifier: Modifier = Modifier,
    user: User,
    messages: List<Message>,
    currentUserId: String
) {
    Column(
        modifier = modifier
            .systemBarsPadding()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RightPaneTopBar(
            user = user
        )
        MessagesList(
            modifier = Modifier.weight(1f),
            messages = messages,
            currentUserId = currentUserId,
            user = user
        )
        MessageInputBar(
            onEmojiClick = {},
            onMicrophoneClick = {},
            onMoreOptionsClick = {}
        )
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
        status = UserStatus.ONLINE
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
        )
    )
    ChatEaseTheme() {
        Column() {
            RightPane(
                user = user,
                messages = messages,
                currentUserId = "user_2",
            )
        }
    }
}
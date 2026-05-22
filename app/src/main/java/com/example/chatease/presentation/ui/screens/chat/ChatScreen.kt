package com.example.chatease.presentation.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    val user = User()
    val messages = listOf<Message>()
    RightPane(
        user = user,
        messages = messages,
        currentUserId = "1"
    )
}
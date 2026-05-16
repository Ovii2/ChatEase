package com.example.chatease.presentation.screens.chats.components.panes.right_pane

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun RightPane(
    modifier: Modifier = Modifier,
    user: User
) {
    Column(modifier = modifier) {
        RightPaneTopBar(
            user = user
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RightPanePreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserStatus.ONLINE
    )
    ChatEaseTheme() {
        Column() {
            RightPane(
                user = user
            )
        }
    }
}
package com.example.chatease.presentation.ui.screens.group_chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Group
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupChatTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val members = 2
    val group = Group(
        conversationId = "1",
        name = "New Groupppppppp",
        imageUrl = null
    )
    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            GroupChatTopBar(
                onBackClick = onBackClick,
                members = members,
                group = group
            )
        }) { paddingValues ->

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
                    onBackClick = {}
                )
            }
        }
    }
}

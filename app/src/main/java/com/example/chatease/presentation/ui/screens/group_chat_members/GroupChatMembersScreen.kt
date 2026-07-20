package com.example.chatease.presentation.ui.screens.group_chat_members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupChatMembersScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.members,
                actionIcon = Icons.Outlined.PersonAdd,
                onActionIconClick = {}
            )
        }) { paddingValues ->

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatMembersScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatMembersScreen(
                    onBackClick = {}
                )
            }
        }
    }
}
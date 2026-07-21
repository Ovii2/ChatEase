package com.example.chatease.presentation.ui.screens.group_chat_members.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupChatMembersScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    currentUserId: String,
    adminIds: List<String>,
    members: List<User>,
    isMemberInContacts: (String) -> Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GroupMembersList(
            currentUserId = currentUserId,
            adminIds = adminIds,
            members = members,
            isMemberInContacts = isMemberInContacts,
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatMembersScreenContentPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatMembersScreenContent(
                    paddingValues = PaddingValues(),
                    currentUserId = "!",
                    adminIds = listOf("1", "2"),
                    members = List(10) {
                        User(
                            uid = it.toString(),
                            fullName = "Test Test",
                            email = "",
                            imageUrl = null,
                            status = UserPresenceStatus.ONLINE,
                            blockedUserIds = emptyList()
                        )
                    },
                    isMemberInContacts = { false }
                )
            }
        }
    }
}

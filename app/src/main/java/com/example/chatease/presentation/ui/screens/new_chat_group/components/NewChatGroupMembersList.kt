package com.example.chatease.presentation.ui.screens.new_chat_group.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.textColor
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatGroupMembersList(
    modifier: Modifier = Modifier,
    onRemoveMember: (String) -> Unit,
    maxMembers: Int,
    members: List<User>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.members_count, members.size, maxMembers),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600
        )
        members.forEach { user ->
            NewChatGroupMemberItem(
                user = user,
                onRemoveMember = onRemoveMember,
            )
        }
    }
}

@Composable
fun NewChatGroupMemberItem(
    modifier: Modifier = Modifier, user: User,
    onRemoveMember: (String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserAvatar(
                user = user
            )
            Column(modifier = Modifier.widthIn(max = 200.dp)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = user.status.toScreenName(),
                    color = user.status.textColor()
                )
            }
        }
        Icon(
            modifier = Modifier.clickable { onRemoveMember(user.uid) },
            imageVector = Icons.Outlined.Close,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewChatGroupMembersListPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "email@test.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    val members = List(10) { user }

    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupMembersList(
                    onRemoveMember = {},
                    maxMembers = 50,
                    members = members,
                )
            }
        }
    }
}
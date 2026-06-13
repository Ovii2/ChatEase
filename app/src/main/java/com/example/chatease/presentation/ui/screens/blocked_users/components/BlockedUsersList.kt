package com.example.chatease.presentation.ui.screens.blocked_users.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun BlockedUsersList(
    modifier: Modifier = Modifier,
    users: List<User>,
    onUnblockUserClick: (String) -> Unit,
) {
    if (users.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.congratulations),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = stringResource(R.string.no_blocks),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.total_blocked, users.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            itemsIndexed(users) { index, user ->
                BlockedUsersListItem(
                    onUnblockUserClick = onUnblockUserClick,
                    user = user,
                    showDivider = index != users.lastIndex
                )
            }
        }
    }
}

@Composable
fun BlockedUsersListItem(
    modifier: Modifier = Modifier,
    onUnblockUserClick: (String) -> Unit,
    user: User,
    showDivider: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.widthIn(max = 250.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Outlined.Block,
                contentDescription = null
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Button(
            onClick = { onUnblockUserClick(user.uid) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.unblock),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    if (showDivider) HorizontalDivider()
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun BlockedUsersListPreview() {
    val users = List(8) {
        User(
            uid = it.toString(),
            fullName = "Test Test",
            email = "test@email.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = listOf()
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BlockedUsersList(
                    users = users,
                    onUnblockUserClick = {}
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.add_members.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.textColor
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AddMembersList(
    modifier: Modifier = Modifier,
    members: List<User>,
    selectedMemberIds: Set<String>,
    onToggleMemberSelection: (String) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        members.forEach { user ->
            AddMembersListItem(
                member = user,
                onToggleUserSelection = { onToggleMemberSelection(user.uid) },
                isChecked = user.uid in selectedMemberIds,
            )
        }
    }
}

@Composable
fun AddMembersListItem(
    modifier: Modifier = Modifier,
    member: User,
    onToggleUserSelection: () -> Unit,
    isChecked: Boolean
) {
    val textColor = if (isChecked) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .height(80.dp)
            .background(
                color = if (isChecked) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                12.dp,
                Alignment.CenterHorizontally
            )
        ) {
            UserAvatar(
                user = member
            )
            Column(modifier = Modifier.widthIn(max = 200.dp)) {
                Text(
                    text = member.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = member.status.toScreenName(),
                    color = member.status.textColor()
                )
            }
        }
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                onToggleUserSelection()
            }
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AddMembersListItemPreview() {
    val user = User(
        uid = "1",
        fullName = "Test Testttttttttttttttttttttttttttttttttttttttttttttttttt",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddMembersListItem(
                    member = user,
                    onToggleUserSelection = {},
                    isChecked = false,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddMembersListPreview() {
    val users = List(10) {
        User(
            uid = it.toString(),
            fullName = "Test Testttttttttttttttttttttttttttttttttttttttttttttttttt",
            email = "",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList()
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddMembersList(
                    members = users,
                    selectedMemberIds = emptySet(),
                    onToggleMemberSelection = {},
                )
            }
        }
    }
}
package com.example.chatease.presentation.ui.screens.group_chat_members.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.textColor
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupMembersList(
    modifier: Modifier = Modifier,
    currentUserId: String,
    ownerId: String,
    adminIds: List<String>,
    members: List<User>,
    isMemberInContacts: (String) -> Boolean,
    onAddAdmin: (String) -> Unit,
    onRemoveAdmin: (String) -> Unit,
    onRemoveMember: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val admins = members.filter { user ->
        user.uid in adminIds
    }

    val regularMembers = members.filterNot { user ->
        user.uid in adminIds
    }

    val currentUserIsOwner = currentUserId == ownerId
    val currentUserIsAdmin = currentUserId in adminIds

    Box(
        modifier = modifier
            .widthIn(max = 600.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.admins_with_count, admins.size),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W600
            )
            admins.forEach { user ->
                GroupMembersListItem(
                    user = user,
                    onAddToContacts = {},
                    onAddAdmin = {},
                    onRemoveAdmin = onRemoveAdmin,
                    onRemoveMember = onRemoveMember,
                    onNavigateToProfile = { onNavigateToProfile(user.uid) },
                    isCurrentUser = user.uid == currentUserId,
                    currentUserIsOwner = currentUserIsOwner,
                    isMemberInContacts = isMemberInContacts(user.uid),
                    memberIsAdmin = user.uid in adminIds,
                    currentUserIsAdmin = currentUserIsAdmin
                )
            }
            Text(
                text = stringResource(R.string.members_with_count, regularMembers.size),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W600
            )
            regularMembers.forEach { user ->
                GroupMembersListItem(
                    user = user,
                    onAddToContacts = {},
                    onAddAdmin = onAddAdmin,
                    onRemoveAdmin = {},
                    onRemoveMember = onRemoveMember,
                    onNavigateToProfile = { onNavigateToProfile(user.uid) },
                    isCurrentUser = user.uid == currentUserId,
                    currentUserIsOwner = currentUserIsOwner,
                    isMemberInContacts = isMemberInContacts(user.uid),
                    memberIsAdmin = user.uid in adminIds,
                    currentUserIsAdmin = currentUserIsAdmin,
                )
            }
        }
    }
}

@Composable
fun GroupMembersListItem(
    modifier: Modifier = Modifier,
    user: User,
    onAddToContacts: (String) -> Unit,
    onAddAdmin: (String) -> Unit,
    onRemoveAdmin: (String) -> Unit,
    onRemoveMember: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    isCurrentUser: Boolean,
    currentUserIsOwner: Boolean,
    isMemberInContacts: Boolean,
    memberIsAdmin: Boolean,
    currentUserIsAdmin: Boolean
) {
    var isMoreOptionsClicked by rememberSaveable { mutableStateOf(false) }
    val canRemoveMember =
        !isCurrentUser && (currentUserIsOwner || currentUserIsAdmin && !memberIsAdmin)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            UserAvatar(
                avatarSize = 50.dp,
                statusBubbleSize = 15.dp,
                initialsFontSize = 21.sp,
                user = user
            )
            Column(modifier = Modifier.widthIn(max = 200.dp)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.status.toScreenName(),
                    color = user.status.textColor()
                )
            }
        }
        Box(
            modifier = modifier,
            contentAlignment = Alignment.CenterEnd
        ) {
            if (!isCurrentUser) {
                Icon(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable {
                            isMoreOptionsClicked = !isMoreOptionsClicked
                        },
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )

                DropdownMenu(
                    expanded = isMoreOptionsClicked,
                    onDismissRequest = { isMoreOptionsClicked = false },
                    offset = DpOffset(x = (-8).dp, y = (1).dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.view_profile)) },
                        onClick = {
                            isMoreOptionsClicked = false
                            onNavigateToProfile()
                        }
                    )
                    if (!isMemberInContacts) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.add_to_contacts)) },
                            onClick = { onAddToContacts(user.uid) }
                        )
                    }
                    if (currentUserIsOwner && !memberIsAdmin) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.promote_admin)
                                )
                            },
                            onClick = { onAddAdmin(user.uid) }
                        )
                    }
                    if (currentUserIsOwner && memberIsAdmin) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.demote_admin)
                                )
                            },
                            onClick = { onRemoveAdmin(user.uid) }
                        )
                    }
                    if (canRemoveMember) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.remove_member),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = { onRemoveMember(user.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupMembersListItemPreview() {
    val user = User(
        uid = "1",
        fullName = "Test Testtttttttttttttttttttttttttttttttttttt",
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
                GroupMembersListItem(
                    user = user,
                    onAddToContacts = {},
                    onAddAdmin = {},
                    onRemoveAdmin = {},
                    onRemoveMember = {},
                    onNavigateToProfile = {},
                    isCurrentUser = false,
                    isMemberInContacts = false,
                    currentUserIsOwner = true,
                    memberIsAdmin = false,
                    currentUserIsAdmin = true,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupMembersListPreview() {
    val members = List(15) {
        User(
            uid = "user_$it",
            fullName = "Test Test",
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
                GroupMembersList(
                    currentUserId = "1",
                    ownerId = "1",
                    adminIds = listOf("user_1", "user_2", "user_3"),
                    members = members,
                    isMemberInContacts = { false },
                    onAddAdmin = {},
                    onRemoveAdmin = {},
                    onRemoveMember = {},
                    onNavigateToProfile = {},
                )
            }
        }
    }
}

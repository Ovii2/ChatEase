package com.example.chatease.presentation.ui.screens.other_user_profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.ElevatedCard
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
import com.example.chatease.domain.model.Group
import com.example.chatease.presentation.ui.screens.shared.group.GroupAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun OtherUserProfileMutualGroupSection(
    modifier: Modifier = Modifier,
    groups: List<Group>
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.mutual_groups),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
            groups.forEach { group ->
                OtherUserProfileMutualGroupItem(
                    group = group
                )
            }
        }
    }
}

@Composable
fun OtherUserProfileMutualGroupItem(
    modifier: Modifier = Modifier,
    group: Group
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GroupAvatar(
                imageUrl = group.imageUrl,
                imageSize = 50.dp
            )
            Column() {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600
                )
                Text(text = stringResource(R.string.total_members, group.userIds.size))
            }
        }
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Item")
@Composable
private fun OtherUserProfileMutualGroupItemPreview() {
    val group = Group(
        conversationId = "1",
        userIds = listOf("1", "2", "3"),
        adminIds = listOf("1"),
        visibleToUserIds = emptyList(),
        ownerId = "1",
        name = "Test Group",
        imageUrl = null,
        removedAtByUserId = mapOf(
            "1" to System.currentTimeMillis()
        )
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileMutualGroupItem(
                    group = group
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtherUserProfileMutualGroupSectionPreview() {
    val groups = List(10) {
        Group(
            conversationId = it.toString(),
            userIds = listOf("1", "2", "3"),
            adminIds = listOf("1"),
            visibleToUserIds = emptyList(),
            ownerId = "1",
            name = "Test Group",
            imageUrl = null,
            removedAtByUserId = mapOf(
                "1" to System.currentTimeMillis()
            )
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileMutualGroupSection(
                    groups = groups
                )
            }
        }
    }
}

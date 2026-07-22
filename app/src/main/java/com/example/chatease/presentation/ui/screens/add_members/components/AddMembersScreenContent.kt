package com.example.chatease.presentation.ui.screens.add_members.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AddMembersScreenContent(
    modifier: Modifier = Modifier,
    members: List<User>,
    onToggleMemberSelection: (String) -> Unit,
    selectedMemberIds: Set<String>,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    val buttonText = when (selectedMemberIds.size) {
        0 -> stringResource(R.string.add_members)
        1 -> stringResource(R.string.add_one_member, selectedMemberIds.size)
        else -> stringResource(R.string.add_multiple_members, selectedMemberIds.size)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            ChatSearchBar(
                value = searchValue,
                onValueChange = onSearchValueChange,
                onClearSearch = onClearSearch,
                placeholder = R.string.search_contacts
            )
            AddMembersList(
                modifier = Modifier.weight(1f),
                members = members,
                selectedMemberIds = selectedMemberIds,
                onToggleMemberSelection = onToggleMemberSelection,
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {}
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun AddMembersScreenContentPreview() {
    val users = List(12) {
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
                AddMembersScreenContent(
                    members = users,
                    selectedMemberIds = emptySet(),
                    onToggleMemberSelection = {},
                    onSearchValueChange = {},
                    searchValue = "Test",
                    onClearSearch = {},
                )
            }
        }
    }
}

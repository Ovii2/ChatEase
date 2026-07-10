package com.example.chatease.presentation.ui.screens.new_chat_group.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatGroupScreenContent(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    paddingValues: PaddingValues,
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    members: List<User>,
    onRemoveMember: (String) -> Unit,
    maxMembers: Int,
    onNavigateToGroupChat: () -> Unit,
    isSuggestGroupNameVisible: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onAcceptGroupNameSuggestion: (String) -> Unit,
    onRefreshGroupNameSuggestion: () -> Unit,
    suggestedGroupName: String,
    groupNameError: Boolean
) {
    val scrollState = rememberScrollState()
    val isScrollable = scrollState.maxValue > 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = modifier
                .widthIn(max = 600.dp)
                .padding(paddingValues)
                .then(if (isScrollable) Modifier.verticalScroll(scrollState) else Modifier)

        ) {
            NewChatGroupTopSection(
                groupName = groupName,
                onGroupNameTextChange = onGroupNameChange,
                isSuggestGroupNameVisible = isSuggestGroupNameVisible,
                onAcceptGroupNameSuggestion = onAcceptGroupNameSuggestion,
                onRefreshGroupNameSuggestion = onRefreshGroupNameSuggestion,
                onFocusChanged = onFocusChanged,
                suggestedGroupName = suggestedGroupName,
                groupNameError = groupNameError,
            )
            NewChatGroupMembersList(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .then(if (!isScrollable) Modifier.weight(1f) else Modifier),
                onRemoveMember = onRemoveMember,
                maxMembers = maxMembers,
                members = members
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = onNavigateToGroupChat,
                shape = RoundedCornerShape(15.dp),
                enabled = members.size > 1 && !groupNameError
            ) {
                Text(text = stringResource(R.string.create_group))
            }
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun NewChatGroupScreenContentPreview() {
    val members = List(5) {
        User(
            uid = it.toString(),
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
                NewChatGroupScreenContent(
                    focusManager = LocalFocusManager.current,
                    paddingValues = PaddingValues(),
                    groupName = "New Group",
                    onGroupNameChange = {},
                    members = members,
                    onRemoveMember = {},
                    maxMembers = 50,
                    onNavigateToGroupChat = {},
                    isSuggestGroupNameVisible = true,
                    onAcceptGroupNameSuggestion = {},
                    onRefreshGroupNameSuggestion = {},
                    onFocusChanged = {},
                    suggestedGroupName = "Test",
                    groupNameError = false,
                )
            }
        }
    }
}

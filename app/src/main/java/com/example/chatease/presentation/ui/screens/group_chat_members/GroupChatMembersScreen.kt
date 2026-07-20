package com.example.chatease.presentation.ui.screens.group_chat_members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.group_chat_members.components.GroupChatMembersScreenContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.error.CommonErrorDisplay
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.GroupChatMembersUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.GroupChatMembersViewModel

@Composable
fun GroupChatMembersScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    conversationId: String,
    groupChatMembersViewModel: GroupChatMembersViewModel = hiltViewModel()
) {
    val uiState by groupChatMembersViewModel.uiState.collectAsState()
    val currentUserId = groupChatMembersViewModel.currentUserId

    LaunchedEffect(conversationId) {
        groupChatMembersViewModel.loadMembers(conversationId)
    }

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
        when (val state = uiState) {
            GroupChatMembersUiState.Loading -> {
                CommonCircularLoader()
            }

            is GroupChatMembersUiState.Success -> {
                GroupChatMembersScreenContent(
                    paddingValues = paddingValues,
                    currentUserId = currentUserId,
                    adminIds = state.adminIds,
                    members = state.members
                )
            }

            is GroupChatMembersUiState.Error -> {
                CommonErrorDisplay(
                    errorText = R.string.fail_load_members
                )
            }
        }
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
                    onBackClick = {},
                    conversationId = "1",
                )
            }
        }
    }
}

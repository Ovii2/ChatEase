package com.example.chatease.presentation.ui.screens.add_members

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.add_members.components.AddMembersScreenContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.AddMembersUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AddMembersViewModel

@Composable
fun AddMembersScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    addMembersViewModel: AddMembersViewModel = hiltViewModel(),
    conversationId: String
) {
    val focusManager = LocalFocusManager.current
    val uiState by addMembersViewModel.uiState.collectAsState()

    LaunchedEffect(conversationId) {
        addMembersViewModel.loadMembers(conversationId)
    }

    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() })
        {
            focusManager.clearFocus()
        },
        contentAlignment = Alignment.TopCenter
    ) {
        Scaffold(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            topBar = {
                CommonTopBar(
                    title = R.string.add_members_group,
                    onBackClick = onBackClick
                )
            }) { paddingValues ->
            when (val state = uiState) {
                AddMembersUiState.Loading -> {
                    CommonCircularLoader()
                }

                is AddMembersUiState.Success -> {
                    AddMembersScreenContent(
                        modifier = Modifier.padding(paddingValues),
                        members = state.members,
                        onToggleMemberSelection = addMembersViewModel::toggleMemberSelection,
                        selectedMemberIds = state.selectedMemberIds
                    )
                }

                is AddMembersUiState.Error -> {}
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddMembersScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddMembersScreen(
                    onBackClick = {},
                    conversationId = "1",
                )
            }
        }
    }
}

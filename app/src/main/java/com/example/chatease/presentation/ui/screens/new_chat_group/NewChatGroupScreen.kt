package com.example.chatease.presentation.ui.screens.new_chat_group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.new_chat_group.components.NewChatGroupMembersList
import com.example.chatease.presentation.ui.screens.new_chat_group.components.NewChatGroupTopSection
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.NewChatGroupViewModel

@Composable
fun NewChatGroupScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToGroupChat: () -> Unit,
    selectedUserIds: List<String>,
    newChatGroupViewModel: NewChatGroupViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val groupName by newChatGroupViewModel.groupName.collectAsState()
    val maxMembers = 50
    val members by newChatGroupViewModel.members.collectAsState()

    LaunchedEffect(selectedUserIds) {
        newChatGroupViewModel.observeMembers(selectedUserIds)
    }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.new_chat_group
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }, contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 600.dp)
                    .padding(bottom = 28.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NewChatGroupTopSection(
                        groupName = groupName,
                        onGroupNameTextChange = newChatGroupViewModel::onGroupNameChange
                    )
                    NewChatGroupMembersList(
                        onRemoveMember = {},
                        maxMembers = maxMembers,
                        members = members
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = onNavigateToGroupChat,
                    shape = RoundedCornerShape(15.dp),
                    enabled = members.size > 1
                ) {
                    Text(
                        modifier = Modifier.clickable {},
                        text = stringResource(R.string.create_group)
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun NewChatGroupScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupScreen(
                    onBackClick = {},
                    onNavigateToGroupChat = {},
                    selectedUserIds = emptyList(),
                )
            }
        }
    }
}
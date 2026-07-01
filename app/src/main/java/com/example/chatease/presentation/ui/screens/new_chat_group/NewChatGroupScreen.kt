package com.example.chatease.presentation.ui.screens.new_chat_group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.new_chat_group.components.NewChatGroupMembersList
import com.example.chatease.presentation.ui.screens.new_chat_group.components.NewChatGroupTopSection
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatGroupScreen(modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    var groupName by rememberSaveable { mutableStateOf("") }
    val maxMembers = 50

    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "email@test.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    val members = List(10) { user }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = {},
                title = R.string.new_chat_group
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NewChatGroupTopSection(
                    groupName = groupName,
                    onGroupNameTextChange = {}
                )
                NewChatGroupMembersList(
                    onRemoveMember = {},
                    maxMembers = maxMembers,
                    members = members
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewChatGroupScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupScreen()
            }
        }
    }
}
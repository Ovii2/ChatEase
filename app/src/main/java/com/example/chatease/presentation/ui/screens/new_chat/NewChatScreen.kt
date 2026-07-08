package com.example.chatease.presentation.ui.screens.new_chat

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.navigation.toScreenName
import com.example.chatease.presentation.ui.screens.new_chat.components.AllContactsSection
import com.example.chatease.presentation.ui.screens.new_chat.components.FrequentlyContactedSection
import com.example.chatease.presentation.ui.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.NewChatViewModel

@Composable
fun NewChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToChatScreen: (String) -> Unit,
    newChatViewModel: NewChatViewModel = hiltViewModel(),
    onNavigateToNewGroupScreen: (Set<String>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val contacts by newChatViewModel.contacts.collectAsState()
    val users by newChatViewModel.users.collectAsState()
    val frequentlyContactedUsers = emptyList<User>()
    var selectedUserIds by rememberSaveable {
        mutableStateOf(setOf<String>())
    }

    Scaffold(
        modifier = modifier.padding(
            vertical = 8.dp,
            horizontal = 12.dp
        ),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = Screens.NewChat.toScreenName(),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    focusManager.clearFocus()
                })
        {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ChatSearchBar(
                    value = "",
                    onValueChange = {},
                    onClearSearch = {},
                    placeholder = R.string.search_contacts
                )
                if (frequentlyContactedUsers.isNotEmpty()) {
                    FrequentlyContactedSection(
                        users = frequentlyContactedUsers
                    )
                }

                if (contacts.isNotEmpty()) {
                    AllContactsSection(
                        users = users,
                        selectedCount = selectedUserIds.size,
                        selectedUserIds = selectedUserIds,
                        onChecked = { userId ->
                            selectedUserIds = if (userId in selectedUserIds) {
                                selectedUserIds - userId
                            } else {
                                selectedUserIds + userId
                            }
                        },
                        onStartChatClick = {
                            newChatViewModel.createNewConversation(
                                selectedUserId = selectedUserIds.first(),
                                onConversationCreated = {
                                    onNavigateToChatScreen(it)
                                }
                            )
                        },
                        onNavigateToNewChatGroupScreen = {
                            onNavigateToNewGroupScreen(
                                selectedUserIds
                            )
                        },
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun NewChatScreenPreview() {
    ChatEaseTheme {
        NewChatScreen(
            onBackClick = {},
            onNavigateToChatScreen = {},
            onNavigateToNewGroupScreen = {},
        )
    }
}
package com.example.chatease.presentation.ui.screens.group_chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupChatScreenContent
import com.example.chatease.presentation.ui.screens.group_chat.components.GroupChatTopBar
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.GroupChatUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.GroupChatViewModel

@Composable
fun GroupChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    conversationId: String,
    groupChatViewModel: GroupChatViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }
    val typingUserIds = listOf("user_1", "user_2")
    val isBlockedByOtherUser = false
    val firstIndex = 0
    val members = 2
    val uiState by groupChatViewModel.uiState.collectAsState()

    LaunchedEffect(conversationId) {
        groupChatViewModel.loadGroupConversation(conversationId)
    }

    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "email@test.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    val currentUserId = "1"

    fun generateRandomUserIds(count: Int): List<String> {
        return (1..10)
            .shuffled()
            .take(count)
            .map { "user_$it" }
    }

    val messages = List(10) {
        Message(
            messageId = it.toString(),
            conversationId = "1",
            senderId = listOf("user_1", "user_2").random(),
            text = LoremIpsum(1).values.first(),
            timeStamp = System.currentTimeMillis(),
            seenBy = generateRandomUserIds(10),
            reactions = emptyMap(),
            messageType = MessageType.TEXT
        )
    }
    val listState = rememberLazyListState()
    val firstUnreadMessageId = "1"
    val firstUserName = user.fullName.substringBefore(" ")
    val secondUsername = user.fullName.substringBefore(" ")

    val typingText = when (typingUserIds.size) {
        1 -> stringResource(R.string.one_is_typing, firstUserName)
        2 -> stringResource(R.string.two_are_typing, firstUserName, secondUsername)
        else -> stringResource(R.string.many_are_typing, typingUserIds.size)
    }

    when (val state = uiState) {
        is GroupChatUiState.Success -> {
            Scaffold(
                modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                topBar = {
                    GroupChatTopBar(
                        onBackClick = onBackClick,
                        members = members,
                        group = state.group
                    )
                }) { paddingValues ->
                GroupChatScreenContent(
                    paddingValues = paddingValues,
                    focusManager = focusManager,
                    user = user,
                    messages = messages,
                    currentUserId = currentUserId,
                    listState = listState,
                    groupMembers = state.members,
                    firstUnreadMessageId = firstUnreadMessageId,
                    typingUserIds = typingUserIds,
                    typingText = typingText,
                    scope = scope,
                    firstIndex = firstIndex,
                    isPeekEnabled = isPeekEnabled,
                    isBlockedByOtherUser = isBlockedByOtherUser
                )
            }
        }

        is GroupChatUiState.Error -> {}
        GroupChatUiState.Loading -> {
            CommonCircularLoader()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatScreen(
                    onBackClick = {},
                    conversationId = "1",
                )
            }
        }
    }
}

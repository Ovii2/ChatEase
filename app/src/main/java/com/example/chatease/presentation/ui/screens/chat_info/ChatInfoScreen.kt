package com.example.chatease.presentation.ui.screens.chat_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.enums.AlertDialogType
import com.example.chatease.presentation.ui.screens.shared.bottom_sheet.CommonChatBottomSheet
import com.example.chatease.presentation.ui.screens.shared.chat.CommonAlertDialog
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.ExtraPane
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.ChatInfoViewModel

@Composable
fun ChatInfoScreen(
    modifier: Modifier = Modifier,
    conversationId: String,
    chatInfoViewModel: ChatInfoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNavigateToHomeScreen: () -> Unit
) {
    val user by chatInfoViewModel.user.collectAsState()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val isConversationCreator by chatInfoViewModel.isConversationCreator.collectAsState()
    var showBlockUserBottomSheet by rememberSaveable { mutableStateOf(false) }
    val isBlockedByMe by chatInfoViewModel.isBlockedByMe.collectAsState()
    val isBlockedByOtherUser by chatInfoViewModel.isBlockedByOtherUser.collectAsState()
    val isConversationDeleted by chatInfoViewModel.isConversationDeleted.collectAsState()

    LaunchedEffect(conversationId) {
        chatInfoViewModel.loadConversation(conversationId)
    }

    LaunchedEffect(isConversationDeleted) {
        if (isConversationDeleted) {
            onNavigateToHomeScreen()
        }
    }

    Scaffold(topBar = {
        CommonTopBar(
            onBackClick = onBackClick,
            title = R.string.chat_info
        )
    }) { paddingValues ->
        ExtraPane(
            modifier = modifier
                .padding(paddingValues)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            user = user,
            onDeleteConversationClick = { showDeleteDialog = true },
            isConversationCreator = isConversationCreator,
            onBlockContactClick = { showBlockUserBottomSheet = true },
            onUnblockContactClick = { chatInfoViewModel.unblockUser(user.uid) },
            isBlockedByMe = isBlockedByMe,
            isBlockedByOtherUser = isBlockedByOtherUser,
        )
        if (showDeleteDialog) {
            CommonAlertDialog(
                title = R.string.confirm_conversation_delete_title,
                bodyText = R.string.confirm_conversation_delete_body,
                dismissButtonText = R.string.dismiss_btn,
                acceptButtonText = R.string.delete_chat,
                onDismiss = { showDeleteDialog = false },
                onAccept = {
                    showDeleteDialog = false
                    chatInfoViewModel.deleteConversation(conversationId)
                },
                alertDialogType = AlertDialogType.CONFIRMATION
            )
        }
    }
    if (showBlockUserBottomSheet) {
        CommonChatBottomSheet(
            onDismiss = { showBlockUserBottomSheet = false },
            onClick = {
                chatInfoViewModel.blockUser(user.uid)
                showBlockUserBottomSheet = false
            },
            title = R.string.block_user_title,
            text = R.string.block_user_message,
            actionButtonText = R.string.block,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatInfoScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChatInfoScreen(
                    conversationId = "1",
                    onBackClick = {},
                    onNavigateToHomeScreen = {},
                )
            }
        }
    }
}
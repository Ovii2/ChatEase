package com.example.chatease.presentation.ui.screens.blocked_users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.blocked_users.components.BlockedUsersList
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.BlockedUsersViewModel

@Composable
fun BlockedUsersScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    blockedUsersViewModel: BlockedUsersViewModel = hiltViewModel()
) {
    val blockedUsers by blockedUsersViewModel.blockedUsers.collectAsState()

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.blocked_users
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            BlockedUsersList(
                modifier = Modifier
                    .padding(paddingValues)
                    .widthIn(max = 600.dp),
                users = blockedUsers,
                onUnblockUserClick = blockedUsersViewModel::unblockUser,
            )
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
private fun BlockedUsersScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BlockedUsersScreen(
                    onBackClick = {}
                )
            }
        }
    }
}

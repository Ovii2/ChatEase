package com.example.chatease.presentation.ui.screens.chat_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.ExtraPane
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.ChatInfoViewModel

@Composable
fun ChatInfoScreen(
    modifier: Modifier = Modifier,
    conversationId: String,
    chatInfoViewModel: ChatInfoViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val user by chatInfoViewModel.user.collectAsState()

    LaunchedEffect(conversationId) {
        chatInfoViewModel.loadConversation(conversationId)
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
                )
            }
        }
    }
}
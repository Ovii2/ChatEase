package com.example.chatease.presentation.ui.screens.group_chat_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.group_chat_info.components.GroupChatInfoScreenContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.GroupChatInfoUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.GroupChatInfoViewModel

@Composable
fun GroupChatInfoScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    groupChatInfoViewModel: GroupChatInfoViewModel = hiltViewModel(),
    conversationId: String
) {
    val uiState by groupChatInfoViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(conversationId) {
        groupChatInfoViewModel.loadGroup(conversationId)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Scaffold(
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 12.dp),
            containerColor = Color.Transparent,
            topBar = {
                CommonTopBar(
                    onBackClick = onBackClick,
                    title = R.string.group_info,
                    transparent = true
                )
            }
        ) { paddingValues ->
            when (val state = uiState) {
                is GroupChatInfoUiState.Error -> {}
                GroupChatInfoUiState.Loading -> {
                    CommonCircularLoader()
                }

                is GroupChatInfoUiState.Success -> {
                    GroupChatInfoScreenContent(
                        paddingValues = paddingValues,
                        group = state.group,
                        members = state.members
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
private fun GroupChatInfoScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatInfoScreen(
                    onBackClick = {},
                    conversationId = "1",
                )
            }
        }
    }
}

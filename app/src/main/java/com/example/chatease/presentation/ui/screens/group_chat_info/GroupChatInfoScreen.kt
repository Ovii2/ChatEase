package com.example.chatease.presentation.ui.screens.group_chat_info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatease.R
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.group_chat_info.components.GroupChatInfoScreenContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.GroupChatInfoUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.GroupChatInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatInfoScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    groupChatInfoViewModel: GroupChatInfoViewModel = hiltViewModel(),
    conversationId: String
) {
    val uiState by groupChatInfoViewModel.uiState.collectAsStateWithLifecycle()
    var isLeavingGroup by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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
                        members = state.members,
                        onLeaveGroup = {
                            isLeavingGroup = true
                        },
                    )
                    if (isLeavingGroup) {
                        LeaveGroupBottomSheet(
                            sheetState = sheetState,
                            onDismissRequest = { isLeavingGroup = false },
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LeaveGroupBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.leave_group_confirmation),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.width(100.dp),
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = stringResource(R.string.no))
                }
                Button(
                    modifier = Modifier.width(100.dp),
                    onClick = {}
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun GroupChatInfoScreenPreview() {
    val group = Group(
        conversationId = "",
        userIds = listOf("user_1", "user_2"),
        ownerId = "1",
        name = "Test Test",
        imageUrl = null
    )

    val members = List(10) {
        User(
            uid = it.toString(),
            fullName = "Test Test",
            email = "",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList()
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            var isLeavingGroup by remember { mutableStateOf(true) }
            val sheetState = rememberModalBottomSheetState()
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatInfoScreenContent(
                    paddingValues = PaddingValues(),
                    group = group,
                    members = members,
                    onLeaveGroup = { isLeavingGroup = true }
                )
                if (isLeavingGroup) {
                    LeaveGroupBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = { isLeavingGroup = false },
                    )
                }
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.group_chat_members

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.group_chat_members.components.GroupChatMembersScreenContent
import com.example.chatease.presentation.ui.screens.shared.bottom_sheet.CommonChatBottomSheet
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.error.CommonErrorDisplay
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.GroupChatMembersUiState
import com.example.chatease.presentation.ui.viewmodel.GroupChatMembersViewModel

@Composable
fun GroupChatMembersScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    conversationId: String,
    groupChatMembersViewModel: GroupChatMembersViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onNavigateToAddMembersScreen: (String) -> Unit,
    onNavigateToProfileScreen: (String) -> Unit
) {
    val uiState by groupChatMembersViewModel.uiState.collectAsState()
    val currentUserId = groupChatMembersViewModel.currentUserId

    val errorMessage = stringResource(R.string.fail_load_members)
    val errorActionLabel = stringResource(R.string.retry)

    val usersInContacts by groupChatMembersViewModel.usersInContacts.collectAsState()

    val state = uiState as? GroupChatMembersUiState.Success

    var memberToRemove by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(conversationId) {
        groupChatMembersViewModel.loadMembers(conversationId)
    }

    LaunchedEffect(uiState) {
        if (uiState is GroupChatMembersUiState.Error) {
            val result = snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = errorActionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                groupChatMembersViewModel.loadMembers(conversationId)
            }
        }
    }

    LaunchedEffect(state?.members?.map { it.uid }) {
        state?.members?.forEach { member ->
            if (member.uid != currentUserId) {
                groupChatMembersViewModel.checkIfMemberIsInContacts(member.uid)
            }
        }
    }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.members,
                actionIcon = Icons.Outlined.PersonAdd,
                onActionIconClick = { onNavigateToAddMembersScreen(conversationId) }
            )
        }) { paddingValues ->
        when (val state = uiState) {
            GroupChatMembersUiState.Loading -> {
                CommonCircularLoader()
            }

            is GroupChatMembersUiState.Success -> {
                GroupChatMembersScreenContent(
                    paddingValues = paddingValues,
                    currentUserId = currentUserId,
                    ownerId = state.ownerId,
                    adminIds = state.adminIds,
                    members = state.members,
                    isMemberInContacts = { memberId ->
                        usersInContacts[memberId] ?: false
                    },
                    onAddAdmin = { groupChatMembersViewModel.addAdmin(conversationId, it) },
                    onRemoveAdmin = { groupChatMembersViewModel.removeAdmin(conversationId, it) },
                    onRemoveMember = { userId ->
                        memberToRemove = userId
                    },
                    onNavigateToProfile = onNavigateToProfileScreen
                )
                if (memberToRemove != null) {
                    CommonChatBottomSheet(
                        onDismiss = { memberToRemove = null },
                        onClick = {
                            groupChatMembersViewModel.removeMember(conversationId, memberToRemove!!)
                            memberToRemove = null
                        },
                        title = R.string.remove_member_title,
                        text = R.string.remove_member_text,
                        actionButtonText = R.string.remove
                    )
                }
            }

            is GroupChatMembersUiState.Error -> {
                CommonErrorDisplay()
            }
        }
    }
}

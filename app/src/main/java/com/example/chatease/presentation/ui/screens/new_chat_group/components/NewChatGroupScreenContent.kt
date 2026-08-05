package com.example.chatease.presentation.ui.screens.new_chat_group.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.auth.AuthActionButton
import com.example.chatease.presentation.ui.state.NewChatGroupUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatGroupScreenContent(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    paddingValues: PaddingValues,
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    members: List<User>,
    onRemoveMember: (String) -> Unit,
    maxMembers: Int,
    isSuggestGroupNameVisible: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onAcceptGroupNameSuggestion: (String) -> Unit,
    onRefreshGroupNameSuggestion: () -> Unit,
    suggestedGroupName: String,
    groupNameError: Boolean,
    onCreateGroup: () -> Unit,
    imageUri: Uri? = null,
    onAddPhotoClick: () -> Unit,
    isUploading: Boolean,
    selectedCategoryId: String?,
    onCategorySelect: (String) -> Unit,
    uiState: NewChatGroupUiState
) {
    val scrollState = rememberScrollState()
    val isScrollable = scrollState.maxValue > 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = modifier
                .widthIn(max = 600.dp)
                .padding(paddingValues)
                .then(if (isScrollable) Modifier.verticalScroll(scrollState) else Modifier),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            NewChatGroupTopSection(
                groupName = groupName,
                onGroupNameTextChange = onGroupNameChange,
                isSuggestGroupNameVisible = isSuggestGroupNameVisible,
                onAcceptGroupNameSuggestion = onAcceptGroupNameSuggestion,
                onRefreshGroupNameSuggestion = onRefreshGroupNameSuggestion,
                onFocusChanged = onFocusChanged,
                suggestedGroupName = suggestedGroupName,
                groupNameError = groupNameError,
                imageUri = imageUri,
                onAddPhotoClick = onAddPhotoClick,
                isUploading = isUploading,
            )
            NewChatGroupCategorySection(
                selectedCategoryId = selectedCategoryId,
                onCategorySelect = onCategorySelect
            )
            NewChatGroupMembersList(
                modifier = Modifier
                    .then(if (!isScrollable) Modifier.weight(1f) else Modifier),
                onRemoveMember = onRemoveMember,
                maxMembers = maxMembers,
                members = members
            )
            AuthActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                buttonText = R.string.create_group,
                isLoading = uiState is NewChatGroupUiState.Loading,
                isSuccess = uiState is NewChatGroupUiState.Success,
                enabled = members.size > 1 && !groupNameError && groupName.isNotBlank(),
                onClick = onCreateGroup,
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun NewChatGroupScreenContentPreview() {
    val members = List(2) {
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
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupScreenContent(
                    focusManager = LocalFocusManager.current,
                    paddingValues = PaddingValues(),
                    groupName = "New Group",
                    onGroupNameChange = {},
                    members = members,
                    onRemoveMember = {},
                    maxMembers = 50,
                    isSuggestGroupNameVisible = true,
                    onAcceptGroupNameSuggestion = {},
                    onRefreshGroupNameSuggestion = {},
                    onFocusChanged = {},
                    suggestedGroupName = "Test",
                    groupNameError = false,
                    onCreateGroup = {},
                    onAddPhotoClick = {},
                    isUploading = false,
                    selectedCategoryId = "2",
                    onCategorySelect = {},
                    uiState = NewChatGroupUiState.Idle,
                )
            }
        }
    }
}

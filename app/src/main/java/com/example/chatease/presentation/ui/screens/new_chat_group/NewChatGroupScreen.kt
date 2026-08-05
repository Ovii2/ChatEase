package com.example.chatease.presentation.ui.screens.new_chat_group

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.new_chat_group.components.NewChatGroupScreenContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.state.NewChatGroupUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.NewChatGroupViewModel
import com.example.chatease.presentation.validation.NewGroupValidator

@Composable
fun NewChatGroupScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToGroupChat: (String) -> Unit,
    selectedUserIds: List<String>,
    newChatGroupViewModel: NewChatGroupViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val groupName by newChatGroupViewModel.groupName.collectAsState()
    val maxMembers = 50
    val members by newChatGroupViewModel.members.collectAsState()
    var isSuggestGroupNameVisible by rememberSaveable { mutableStateOf(false) }
    val suggestedGroupName by newChatGroupViewModel.suggestedGroupName.collectAsState()
    var isFieldTouched by rememberSaveable { mutableStateOf(false) }
    val groupNameError = NewGroupValidator.validateNewGroupName(groupName) && isFieldTouched
    val imageUri by newChatGroupViewModel.groupImageUri.collectAsState()
    val isUploading by newChatGroupViewModel.isUploadingImage.collectAsState()
    val selectedCategoryId by newChatGroupViewModel.selectedCategoryId.collectAsState()
    val uiState by newChatGroupViewModel.uiState.collectAsState()

    LaunchedEffect(selectedUserIds) {
        newChatGroupViewModel.observeMembers(selectedUserIds)
    }

    LaunchedEffect(Unit) {
        newChatGroupViewModel.suggestGroupName()
        newChatGroupViewModel.resetState()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            newChatGroupViewModel.updateGroupProfileImage(it)
        }
    }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.new_chat_group
            )
        }) { paddingValues ->
        NewChatGroupScreenContent(
            focusManager = focusManager,
            paddingValues = paddingValues,
            groupName = groupName,
            onGroupNameChange = newChatGroupViewModel::onGroupNameChange,
            members = members,
            onRemoveMember = newChatGroupViewModel::removeMember,
            maxMembers = maxMembers,
            isSuggestGroupNameVisible = isSuggestGroupNameVisible,
            onAcceptGroupNameSuggestion = {
                newChatGroupViewModel.acceptSuggestedGroupName(suggestedGroupName)
                focusManager.clearFocus()
            },
            onRefreshGroupNameSuggestion = newChatGroupViewModel::refreshSuggestGroupName,
            onFocusChanged = { isFocused ->
                isSuggestGroupNameVisible = isFocused
                if (isFocused) {
                    isFieldTouched = true
                }
            },
            suggestedGroupName = suggestedGroupName,
            groupNameError = groupNameError,
            onCreateGroup = {
                newChatGroupViewModel.createGroup { conversationId ->
                    onNavigateToGroupChat(
                        conversationId
                    )
                }
            },
            imageUri = imageUri,
            onAddPhotoClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            isUploading = isUploading,
            selectedCategoryId = selectedCategoryId,
            onCategorySelect = newChatGroupViewModel::selectCategory,
            uiState = uiState,
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun NewChatGroupScreenPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    val members = List(5) { user }
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
                    isSuggestGroupNameVisible = false,
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
                    uiState = NewChatGroupUiState.Success,
                )
            }
        }
    }
}

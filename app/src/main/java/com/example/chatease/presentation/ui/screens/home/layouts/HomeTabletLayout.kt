package com.example.chatease.presentation.ui.screens.home.layouts

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.Message
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.ExtraPane
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.LeftPane
import com.example.chatease.presentation.ui.screens.shared.panes.right_pane.RightPane
import com.example.chatease.presentation.ui.state.ChatPaneUiState
import com.example.chatease.presentation.ui.state.FileDownloadUiState
import com.example.chatease.presentation.ui.state.HomeUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeTabletLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    state: HomeUiState.Success,
    selectedCategory: String,
    focusManager: FocusManager,
    messages: List<Message>,
    isConversationCreator: Boolean,
    onSelectCategory: (String) -> Unit,
    onConversationClick: (String, Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentUserId: String,
    onBackClick: () -> Unit,
    onSendMessageClick: (String, Message?) -> Unit,
    firstUnreadMessageId: String?,
    onMessagesVisible: () -> Unit,
    onReactionClick: (String, String) -> Unit,
    onNavigateToChatInfo: () -> Unit,
    isBlockedByOtherUser: Boolean,
    isBlockedByMe: Boolean,
    chatPaneUiState: ChatPaneUiState,
    onNavigateToGroupChatInfo: (String) -> Unit,
    onLongClick: (String, Boolean) -> Unit,
    onViewContactClick: (String) -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onSendFile: (Uri) -> Unit,
    onFileClick: (Message) -> Unit,
    onImageClick: (FileAttachment) -> Unit,
    onSendImages: (List<Uri>) -> Unit,
    onDownloadClick: (Message) -> Unit,
    uploadingFileId: String?,
    fileUploadProgress: Float?,
    pendingFileMessage: Message?,
    fileDownloadUiState: FileDownloadUiState,
    snackbarHostState: SnackbarHostState,
    mediaItems: List<MediaItem>,
    onNavigateToMediaAndDocsScreen: () -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
    var isPeekEnabled by rememberSaveable { mutableStateOf(false) }

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                LeftPane(
                    modifier = modifier
                        .padding(paddingValues)
                        .padding(vertical = 8.dp),
                    user = state.user,
                    categories = state.categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = onSelectCategory,
                    onConversationClick = { conversationId, isGroup ->
                        onConversationClick(conversationId, isGroup)

                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    onClickToSeeAll = {},
                    conversations = state.conversations,
                    focusManager = focusManager,
                    onLogoutClick = onLogoutClick,
                    onNavigateToProfile = onNavigateToProfile,
                    onLongClick = onLongClick,
                    searchValue = searchValue,
                    onSearchValueChange = onSearchValueChange,
                    currentUserId = currentUserId,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                RightPane(
                    messages = messages,
                    currentUserId = currentUserId,
                    onBackClick = onBackClick,
                    onSendMessageClick = onSendMessageClick,
                    firstUnreadMessageId = firstUnreadMessageId,
                    onMessagesVisible = onMessagesVisible,
                    onReactionClick = onReactionClick,
                    onNavigateToChatInfo = onNavigateToChatInfo,
                    isPeekEnabled = isPeekEnabled,
                    onPeekClick = { isPeekEnabled = !isPeekEnabled },
                    typingUserIds = listOf(),
                    updateTypingStatus = { },
                    isBlockedByOtherUser = isBlockedByOtherUser,
                    onStartAudioCall = {},
                    chatPaneUiState = chatPaneUiState,
                    onNavigateToGroupChatInfo = onNavigateToGroupChatInfo,
                    onShowUsersReactionsClick = {},
                    onSendFile = onSendFile,
                    onFileClick = onFileClick,
                    onSendImages = onSendImages,
                    onDownloadClick = onDownloadClick,
                    uploadingFileId = uploadingFileId,
                    fileUploadProgress = fileUploadProgress,
                    pendingFileMessage = pendingFileMessage,
                    fileDownloadUiState = fileDownloadUiState,
                    snackbarHostState = snackbarHostState,
                    onImageClick = onImageClick,
                )
            }
        },
        extraPane = {
            AnimatedPane {
                ExtraPane(
                    user = state.user,
                    onDeleteConversationClick = {},
                    isConversationCreator = isConversationCreator,
                    onBlockContactClick = {},
                    onUnblockContactClick = {},
                    isBlockedByMe = isBlockedByMe,
                    isBlockedByOtherUser = isBlockedByOtherUser,
                    onViewContactClick = onViewContactClick,
                    currentUserId = currentUserId,
                    mediaItems = mediaItems,
                    onNavigateToMediaAndDocsScreen = onNavigateToMediaAndDocsScreen
                )
            }
        }
    )
}

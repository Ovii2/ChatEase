package com.example.chatease.presentation.ui.screens.home.layouts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.example.chatease.presentation.ui.screens.shared.panes.left_pane.LeftPane
import com.example.chatease.presentation.ui.state.HomeUiState

@Composable
fun HomeCompactLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    state: HomeUiState.Success,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onConversationClick: (String) -> Unit,
    focusManager: FocusManager,
    onLogoutClick: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    LeftPane(
        modifier = modifier
            .padding(paddingValues)
            .padding(vertical = 8.dp),
        user = state.user,
        categories = state.categories,
        selectedCategory = selectedCategory,
        onSelectCategory = onSelectCategory,
        onConversationClick = onConversationClick,
        onClickToSeeAll = {},
        conversations = state.conversations,
        focusManager = focusManager,
        onLogoutClick = onLogoutClick,
        onNavigateToProfile = onNavigateToProfile
    )
}
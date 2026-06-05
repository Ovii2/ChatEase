package com.example.chatease.presentation.ui.screens.my_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
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
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ThemeMode
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.ProfileStatUiModel
import com.example.chatease.presentation.ui.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileLogoutButton
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileSettingsSection
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileStatsRow
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileTopSection
import com.example.chatease.presentation.ui.state.MyProfileUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AppSettingsViewModel
import com.example.chatease.presentation.ui.viewmodel.AuthViewModel
import com.example.chatease.presentation.ui.viewmodel.MyProfileViewModel

@Composable
fun MyProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    myProfileViewModel: MyProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    appSettingsViewModel: AppSettingsViewModel = hiltViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onThemeToggleClick: () -> Unit
) {
    val themeMode by appSettingsViewModel.themeMode.collectAsState()
    val actionIcon =
        when (themeMode) {
            ThemeMode.LIGHT -> Icons.Outlined.DarkMode
            ThemeMode.DARK -> Icons.Outlined.LightMode
        }

    val uiState by myProfileViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            ContactsScreenTopBar(
                onBackClick = onBackClick,
                actionIcon = actionIcon,
                onActionIconClick = onThemeToggleClick
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            MyProfileScreenContent(
                uiState = uiState,
                onLogoutClick = {
                    authViewModel.logout()
                    onNavigateToLoginScreen()
                },
            )
        }
    }
}


@Composable
fun MyProfileScreenContent(
    modifier: Modifier = Modifier,
    uiState: MyProfileUiState,
    onLogoutClick: () -> Unit
) {
    when (uiState) {
        is MyProfileUiState.Error -> {}
        MyProfileUiState.Loading -> {}
        is MyProfileUiState.Success -> {
            MyProfileTopSection(
                user = uiState.user
            )
            MyProfileStatsRow(
                stats = uiState.stats
            )
            MyProfileSettingsSection()
            MyProfileLogoutButton(
                onLogoutClick = onLogoutClick
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyProfileScreenPreview() {
    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.AWAY
    )
    val stats = List(3) {
        ProfileStatUiModel(
            value = (it * 1_000_000).toString(),
            label = R.string.chats
        )
    }
    val successState = MyProfileUiState.Success(
        user = user,
        stats = stats,
        isUploadingImage = false
    )
    ChatEaseTheme {
        Scaffold(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                MyProfileScreenContent(
                    uiState = successState,
                    onLogoutClick = {},
                )
            }
        }
    }
}

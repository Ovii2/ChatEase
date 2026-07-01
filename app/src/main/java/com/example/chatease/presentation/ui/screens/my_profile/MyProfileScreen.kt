package com.example.chatease.presentation.ui.screens.my_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.domain.model.enums.ThemeMode
import com.example.chatease.presentation.ui.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileLogoutButton
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileSettingsSection
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileStatsRow
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileTopSection
import com.example.chatease.presentation.ui.state.MyProfileUiState
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
    onThemeToggleClick: () -> Unit,
    onNavigateToPrivacyAndSecurity: () -> Unit,

    ) {
    val themeMode by appSettingsViewModel.themeMode.collectAsState()
    val actionIcon =
        when (themeMode) {
            ThemeMode.SYSTEM_DEFAULT -> Icons.Outlined.BrightnessAuto
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                MyProfileScreenContent(
                    uiState = uiState,
                    onLogoutClick = {
                        authViewModel.logout()
                        onNavigateToLoginScreen()
                    },
                    onNavigateToBlockedUsers = onNavigateToPrivacyAndSecurity,
                )
            }
        }
    }
}


@Composable
fun MyProfileScreenContent(
    modifier: Modifier = Modifier,
    uiState: MyProfileUiState,
    onLogoutClick: () -> Unit,
    onNavigateToBlockedUsers: () -> Unit
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
            MyProfileSettingsSection(
                onNavigateToPrivacyAndSecurity = onNavigateToBlockedUsers
            )
            MyProfileLogoutButton(
                onLogoutClick = onLogoutClick
            )
        }
    }
}

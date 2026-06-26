package com.example.chatease.presentation.ui.screens.calls

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.screens.calls.components.CallsList
import com.example.chatease.presentation.ui.screens.shared.chat.ChatNavigationScaffold
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.loading.CommonCircularLoader
import com.example.chatease.presentation.ui.state.CallsUiState
import com.example.chatease.presentation.ui.state.HomeUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.CallViewModel
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel
import com.example.chatease.presentation.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CallsScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onBackClick: () -> Unit,
    callViewModel: CallViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    contactsViewModel: ContactsViewModel = hiltViewModel(),
) {
    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)
    val homeUiState by homeViewModel.uiState.collectAsState()
    val unreadMessages = (homeUiState as? HomeUiState.Success)?.unreadMessages ?: 0
    val pendingRequests by contactsViewModel.pendingRequests.collectAsState()
    val missedCalls = 1
    val callsUiState by callViewModel.uiState.collectAsState()
    val callHistoryUiModels =
        (callsUiState as? CallsUiState.Success)?.callHistoryList ?: emptyList()

    LaunchedEffect(Unit) {
        callViewModel.observeCallHistory()
    }

    ChatNavigationScaffold(
        windowSizeClass = windowSizeClass,
        currentRoute = currentRoute,
        unreadMessages = unreadMessages,
        pendingRequests = pendingRequests.size,
        missedCalls = missedCalls,
        onNavigateToHome = onNavigateToHome,
        onNavigateToContacts = onNavigateToContacts,
        onNavigateToCalls = {},
        onNavigateToProfile = onNavigateToProfile
    ) {
        Scaffold(
            modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            topBar = {
                CommonTopBar(
                    onBackClick = onBackClick,
                    title = R.string.calls
                )
            }) { paddingValues ->
            when (callsUiState) {
                is CallsUiState.Error -> {}
                CallsUiState.Loading -> {
                    CommonCircularLoader()
                }

                is CallsUiState.Success -> {
                    CallsList(
                        modifier = Modifier.padding(paddingValues),
                        callHistoryUiModels = callHistoryUiModels
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CallsScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CallsScreen(
                    onBackClick = {},
                    currentRoute = Screens.Calls.route,
                    onNavigateToHome = {},
                    onNavigateToContacts = {},
                    onNavigateToProfile = {},
                )
            }
        }
    }
}

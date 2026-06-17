package com.example.chatease.presentation.ui.screens.calls

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.screens.calls.components.CallsList
import com.example.chatease.presentation.ui.screens.shared.chat.ChatNavigationScaffold
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CallsScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onBackClick: () -> Unit
) {
    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)
    val unreadMessages = 1
    val pendingRequests = 1
    val missedCalls = 1
    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    val callHistories = emptyList<CallHistory>()

    ChatNavigationScaffold(
        windowSizeClass = windowSizeClass,
        currentRoute = currentRoute,
        unreadMessages = unreadMessages,
        pendingRequests = pendingRequests,
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
            CallsList(
                modifier = Modifier.padding(paddingValues),
                user = user,
                callHistories = callHistories
            )
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

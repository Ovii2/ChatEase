package com.example.chatease.presentation.ui.screens.all_requests

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.navigation.toScreenName
import com.example.chatease.presentation.ui.screens.all_requests.components.AllRequestsTab
import com.example.chatease.presentation.ui.screens.all_requests.components.ReceivedRequestsSection
import com.example.chatease.presentation.ui.screens.sent_requests.components.SentRequestsContent
import com.example.chatease.presentation.ui.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.state.ReceivedRequestsUiState
import com.example.chatease.presentation.ui.state.SentRequestsUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AllRequestsViewModel

@Composable
fun AllRequestsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    allRequestsViewModel: AllRequestsViewModel = hiltViewModel()
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val receivedContactRequests by allRequestsViewModel.receivedRequests.collectAsState()
    val sentContactRequests by allRequestsViewModel.sentRequests.collectAsState()

    val receivedCount = when (val state = receivedContactRequests) {
        is ReceivedRequestsUiState.Success -> state.requests.size
        else -> 0
    }

    val sentCount = when (val state = sentContactRequests) {
        is SentRequestsUiState.Success -> state.requests.size
        else -> 0
    }

    var searchValue by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val successState = receivedContactRequests as? ReceivedRequestsUiState.Success
    val filteredReceivedRequests =
        if (successState != null) {
            ReceivedRequestsUiState.Success(
                successState.requests.filter {
                    it.user.fullName.contains(other = searchValue, ignoreCase = true)
                }
            )
        } else {
            receivedContactRequests
        }

    val sentSuccessState = sentContactRequests as? SentRequestsUiState.Success
    val filteredSentRequests =
        if (sentSuccessState != null) {
            SentRequestsUiState.Success(
                sentSuccessState.requests.filter {
                    it.receiver.fullName.contains(
                        other = searchValue,
                        ignoreCase = true
                    )
                }
            )
        } else {
            sentContactRequests
        }

    val shouldShowSearch = if (selectedTabIndex == 0) receivedCount > 1 else sentCount > 1

    Scaffold(
        modifier = modifier.padding(8.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = Screens.AllRequests.toScreenName()
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    AllRequestsTab(
                        selected = selectedTabIndex == 0,
                        onTabClick = {
                            selectedTabIndex = 0
                        },
                        title = R.string.received,
                        count = receivedCount
                    )
                    AllRequestsTab(
                        selected = selectedTabIndex == 1,
                        onTabClick = {
                            selectedTabIndex = 1
                        },
                        title = R.string.sent,
                        count = sentCount
                    )
                }
                if (shouldShowSearch) {
                    ChatSearchBar(
                        value = searchValue,
                        onValueChange = { searchValue = it },
                        onClearSearch = { searchValue = "" },
                        placeholder = R.string.search_requests
                    )
                }
                if (selectedTabIndex == 0) {
                    ReceivedRequestsSection(
                        receivedContactRequests = filteredReceivedRequests,
                        onDismissRequestClick = allRequestsViewModel::declineContactRequest,
                        onAcceptRequestClick = allRequestsViewModel::acceptContactRequest
                    )
                } else {
                    SentRequestsContent(
                        paddingValues = PaddingValues(),
                        sentRequests = filteredSentRequests,
                        onWithdrawRequest = allRequestsViewModel::withDrawContactRequest
                    )
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AllRequestsScreenPreview() {
    ChatEaseTheme {
        Scaffold {
            Column {
                AllRequestsScreen(
                    onBackClick = {}
                )
            }
        }
    }
}
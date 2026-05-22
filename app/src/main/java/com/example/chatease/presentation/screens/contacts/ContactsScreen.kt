package com.example.chatease.presentation.screens.contacts

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserStatus
import com.example.chatease.presentation.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.screens.contacts.components.ContactsSearchResultsRow
import com.example.chatease.presentation.screens.contacts.components.MyContactsSection
import com.example.chatease.presentation.screens.contacts.components.PendingRequestsSection
import com.example.chatease.presentation.screens.contacts.components.SentRequestsButton
import com.example.chatease.presentation.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSentRequests: () -> Unit,
    onBackClick: () -> Unit,
    contactsViewModel: ContactsViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val searchValue by contactsViewModel.searchValue.collectAsState()
    val searchedUsers by contactsViewModel.searchedUsers.collectAsState()
    val sentRequests by contactsViewModel.sentRequests.collectAsState()
    val pendingRequests by contactsViewModel.pendingRequests.collectAsState()
    val currentUserId = contactsViewModel.currentUserId ?: ""
    val pendingRequestLimit = 3
    val users = listOf(
        User(
            uid = "10",
            fullName = "Some User",
            email = "some@user.com",
            imageUrl = null,
            status = UserStatus.OFFLINE
        )
    )

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            ContactsScreenTopBar(
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                })
        {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ChatSearchBar(
                    value = searchValue,
                    onValueChange = contactsViewModel::onSearchValueChange,
                    onClearSearch = contactsViewModel::clearSearch,
                    placeholder = R.string.search_contacts
                )
                if (searchValue.isNotBlank()) {
                    ContactsSearchResultsRow(
                        users = searchedUsers.take(5),
                        onAddContactClick = contactsViewModel::sendContactRequest,
                        currentUserId = currentUserId,
                        sentRequests = sentRequests
                    )
                }
                if (sentRequests.isNotEmpty()) {
                    SentRequestsButton(
                        onNavigateToSentRequests = onNavigateToSentRequests,
                        sentRequestsCount = sentRequests.size
                    )
                }
                if (pendingRequests.isNotEmpty()) {
                    PendingRequestsSection(
                        onViewAllRequests = {},
                        pendingRequests = pendingRequests.take(pendingRequestLimit),
                        onDismissRequestClick = {},
                        onAcceptRequestClick = {},
                        pendingRequestsCount = pendingRequests.size,
                        pendingRequestLimit = pendingRequestLimit
                    )
                }
                MyContactsSection(
                    users = users,
                    onContactClick = {}
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ContactsScreenPreview() {
    ChatEaseTheme() {
        Scaffold() {
            ContactsScreen(
                onNavigateToSentRequests = {},
                onBackClick = {},
            )
        }
    }
}

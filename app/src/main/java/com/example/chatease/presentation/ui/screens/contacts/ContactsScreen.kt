package com.example.chatease.presentation.ui.screens.contacts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.navigation.toScreenName
import com.example.chatease.presentation.ui.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.ui.screens.contacts.components.ContactsSearchResultsRow
import com.example.chatease.presentation.ui.screens.contacts.components.MyContactsSection
import com.example.chatease.presentation.ui.screens.contacts.components.PendingRequestsSection
import com.example.chatease.presentation.ui.screens.contacts.components.SentRequestsButton
import com.example.chatease.presentation.ui.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.viewmodel.ContactsViewModel

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSentRequests: () -> Unit,
    onNavigateToAllRequests: () -> Unit,
    onBackClick: () -> Unit,
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    onNavigateToOtherUserProfile: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val searchValue by contactsViewModel.searchValue.collectAsState()
    val searchedUsers by contactsViewModel.searchedUsers.collectAsState()
    val sentRequests by contactsViewModel.sentRequests.collectAsState()
    val pendingRequests by contactsViewModel.pendingRequests.collectAsState()
    val cooldownRequests by contactsViewModel.cooldowns.collectAsState()
    val currentUserId = contactsViewModel.currentUserId ?: ""
    val pendingRequestLimit = 3
    val receivedRequestUserIds by contactsViewModel.receivedRequestUserIds.collectAsState()
    val contacts by contactsViewModel.contacts.collectAsState()
    var isVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contactsViewModel.getSentRequests()
        contactsViewModel.getContacts()
        isVisible = true
    }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            ContactsScreenTopBar(
                onBackClick = onBackClick,
                title = Screens.Contacts.toScreenName(),
                actionIcon = Icons.Outlined.PersonAdd,
                onActionIconClick = {},
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
                        onAcceptContactClick = {},
                        currentUserId = currentUserId,
                        sentRequests = sentRequests,
                        cooldownRequests = cooldownRequests,
                        receivedRequestUserIds = receivedRequestUserIds,
                        contactIds = contacts.map { it.uid },
                    )
                }
                if (sentRequests.isNotEmpty()) {
                    SentRequestsButton(
                        onNavigateToSentRequests = {
                            onNavigateToSentRequests()
                            contactsViewModel.clearSearch()
                            focusManager.clearFocus()
                        },
                        sentRequestsCount = sentRequests.size
                    )
                }
                if (pendingRequests.isNotEmpty()) {
                    PendingRequestsSection(
                        onViewAllRequests = onNavigateToAllRequests,
                        pendingRequests = pendingRequests.take(pendingRequestLimit),
                        onDismissRequestClick = {
                            contactsViewModel.declineContactRequest(it)
                        },
                        onAcceptRequestClick = { requestId ->
                            contactsViewModel.acceptContactRequest(requestId)
                        },
                        pendingRequestsCount = pendingRequests.size,
                        pendingRequestLimit = pendingRequestLimit
                    )
                }
                if (contacts.isNotEmpty()) {
                    MyContactsSection(
                        contacts = contacts,
                        onContactClick = { onNavigateToOtherUserProfile(it) }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                animationSpec = tween(500),
                                initialOffsetY = { -it })
                        ) {
                            Text(
                                text = stringResource(R.string.no_contacts),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                animationSpec = tween(500, delayMillis = 250),
                                initialOffsetY = { -it })
                        ) {
                            Text(
                                text = stringResource(R.string.all_contacts_shown_here),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

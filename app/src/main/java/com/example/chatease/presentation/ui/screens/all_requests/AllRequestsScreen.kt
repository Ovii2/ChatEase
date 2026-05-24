package com.example.chatease.presentation.ui.screens.all_requests

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.UserHeaderStatusType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.all_requests.components.AllRequestsTab
import com.example.chatease.presentation.ui.screens.all_requests.components.ReceivedRequestsSection
import com.example.chatease.presentation.ui.screens.contacts.components.PendingItemRequestButton
import com.example.chatease.presentation.ui.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.user.UserHeader
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AllRequestsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onReceivedTabClick: () -> Unit,
    onSentTabClick: () -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val contactRequests = List(10) {
        ContactRequest(
            id = it.toString(),
            senderUserId = it.toString(),
            receiverUserId = it.toString(),
            timestamp = System.currentTimeMillis(),
            status = ContactRequestStatus.PENDING
        )
    }

    val user = User(
        uid = "1",
        fullName = "Test Tester",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )


    Scaffold(
        modifier = modifier.padding(8.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.all_requests
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                AllRequestsTab(
                    selected = selectedTabIndex == 0,
                    onTabClick = onReceivedTabClick,
                    title = R.string.received,
                    count = 125,
                )
                AllRequestsTab(
                    selected = selectedTabIndex == 1,
                    onTabClick = onSentTabClick,
                    title = R.string.sent,
                    count = 4
                )
            }
            ChatSearchBar(
                value = "",
                onValueChange = {},
                onClearSearch = {},
                placeholder = R.string.search_requests
            )
            Text(
                text = stringResource(R.string.all_requests_column_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            ReceivedRequestsSection(
                contactRequests = contactRequests,
                user = user,
                onDismissRequestClick = {},
                onAcceptRequestClick = {}
            )
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AllRequestsScreenPreview() {
    ChatEaseTheme() {
        Scaffold() {
            Column() {
                AllRequestsScreen(
                    onBackClick = {},
                    onReceivedTabClick = {},
                    onSentTabClick = {},
                )
            }
        }
    }
}
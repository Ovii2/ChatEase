package com.example.chatease.presentation.ui.screens.all_requests.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.chatease.presentation.ui.screens.contacts.components.PendingItemRequestButton
import com.example.chatease.presentation.ui.screens.shared.user.UserHeader
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReceivedRequestsSection(
    modifier: Modifier = Modifier,
    contactRequests: List<ContactRequest>,
    user: User,
    onDismissRequestClick: () -> Unit,
    onAcceptRequestClick: () -> Unit
) {
    if (contactRequests.isNotEmpty()) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(contactRequests) { request ->
                AllRequestsItem(
                    user = user,
                    onDismissRequestClick = onDismissRequestClick,
                    onAcceptRequestClick = onAcceptRequestClick,
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.title_empty_requests),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.label_empty_requests),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AllRequestsItem(
    modifier: Modifier = Modifier,
    user: User,
    onDismissRequestClick: () -> Unit,
    onAcceptRequestClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserHeader(
                    user = user,
                    avatarSize = 50.dp,
                    initialsFontSize = 23.sp,
                    contactRequestStatus = ContactRequestStatus.PENDING,
                    statusType = UserHeaderStatusType.REQUEST
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PendingItemRequestButton(
                        onClick = onDismissRequestClick,
                        icon = Icons.Default.Close,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    PendingItemRequestButton(
                        onClick = onAcceptRequestClick,
                        icon = Icons.Default.Check,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReceivedRequestsSectionPreview() {
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
    ChatEaseTheme() {
        Scaffold() { paddingValues ->
            ReceivedRequestsSection(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(paddingValues),
                contactRequests = contactRequests,
                user = user,
                onDismissRequestClick = {},
                onAcceptRequestClick = {}
            )
        }
    }
}
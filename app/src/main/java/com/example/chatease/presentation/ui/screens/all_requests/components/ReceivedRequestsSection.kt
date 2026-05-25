package com.example.chatease.presentation.ui.screens.all_requests.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.UserHeaderStatusType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.example.chatease.presentation.ui.screens.contacts.components.PendingItemRequestButton
import com.example.chatease.presentation.ui.screens.shared.user.UserHeader
import com.example.chatease.presentation.ui.state.ReceivedRequestsUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReceivedRequestsSection(
    modifier: Modifier = Modifier,
    receivedContactRequests: ReceivedRequestsUiState,
    onDismissRequestClick: () -> Unit,
    onAcceptRequestClick: () -> Unit
) {
    when (receivedContactRequests) {
        ReceivedRequestsUiState.Loading -> {
            // Todo: "Add shimmering"
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Loading")
            }
        }

        is ReceivedRequestsUiState.Success -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.all_requests_column_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                items(receivedContactRequests.requests) { request ->
                    AllRequestsItem(
                        user = request.user,
                        onDismissRequestClick = onDismissRequestClick,
                        onAcceptRequestClick = onAcceptRequestClick,
                    )
                }
            }
        }

        ReceivedRequestsUiState.Empty -> {
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

        is ReceivedRequestsUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                        .size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    text = stringResource(receivedContactRequests.errorMessage),
                    style = MaterialTheme.typography.labelLarge
                )
            }
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
            .height(90.dp)
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
    val user = User(
        uid = "1",
        fullName = "Test Tester",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val contactRequests = List(10) {
        PendingRequestUiModel(
            requestId = "1",
            user = user
        )
    }

    val successState = ReceivedRequestsUiState.Success(
        requests = contactRequests
    )

    val errorState = ReceivedRequestsUiState.Error(
        errorMessage = R.string.fail_load_received_requests
    )


    ChatEaseTheme() {
        Scaffold() { paddingValues ->
            ReceivedRequestsSection(
                modifier = Modifier
                    .padding(paddingValues),
                receivedContactRequests = successState,
                onDismissRequestClick = {},
                onAcceptRequestClick = {}
            )
        }
    }
}
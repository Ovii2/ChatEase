package com.example.chatease.presentation.ui.screens.sent_requests.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.SentRequestUiModel
import com.example.chatease.presentation.ui.screens.shared.shimmer.ShimmerContactRequestsSection
import com.example.chatease.presentation.ui.state.SentRequestsUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun SentRequestsContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    sentRequests: SentRequestsUiState,
    onWithdrawRequest: (String) -> Unit
) {
    when (sentRequests) {
        SentRequestsUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShimmerContactRequestsSection(
                    columns = 1,
                    isReceivedRequest = false
                )
            }
        }

        SentRequestsUiState.Empty -> {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_sent_requests),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(R.string.when_you_send),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        is SentRequestsUiState.Success -> {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.label_sent_requests),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                        fontSize = 15.sp
                    )
                }
                items(
                    items = sentRequests.requests,
                    key = { it.requestId }
                ) { sentRequest ->
                    SentRequestsItem(
                        user = sentRequest.receiver,
                        contactRequestStatus = sentRequest.status,
                        onWithdrawRequest = {
                            onWithdrawRequest(sentRequest.requestId)
                        }
                    )
                }
            }
        }

        is SentRequestsUiState.Error -> {
            Text(text = "Failed to load")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SentRequestsContentPreview() {
    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val requests = List(10) {
        SentRequestUiModel(
            requestId = it.toString(),
            receiver = user,
            status = ContactRequestStatus.PENDING
        )
    }

    val loadingState = SentRequestsUiState.Loading
    val successState = SentRequestsUiState.Success(
        requests = requests
    )
    ChatEaseTheme() {
        Surface() {
            SentRequestsContent(
                paddingValues = PaddingValues(),
                sentRequests = loadingState,
                onWithdrawRequest = {}
            )
        }
    }
}
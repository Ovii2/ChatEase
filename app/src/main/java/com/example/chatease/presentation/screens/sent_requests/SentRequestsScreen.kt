package com.example.chatease.presentation.screens.sent_requests

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.UserHeaderStatusType
import com.example.chatease.presentation.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.screens.shared.user.UserHeader
import com.example.chatease.presentation.ui.state.SentRequestsUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.SentRequestsViewModel

@Composable
fun SentRequestsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    sentRequestsViewModel: SentRequestsViewModel = hiltViewModel()
) {
    val sentRequests by sentRequestsViewModel.sentRequests.collectAsState()

    Scaffold(
        modifier = modifier.padding(8.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.sent_requests,
            )
        }
    ) { paddingValues ->
        when (val uiState = sentRequests) {
            SentRequestsUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator()
                    Text(text = stringResource(R.string.loading))
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
                    items(uiState.requests) { sentRequest ->
                        SentRequestsItem(
                            user = sentRequest.receiver,
                            contactRequestStatus = sentRequest.status,
                            onWithdrawRequest = {}
                        )
                    }
                }
            }

            is SentRequestsUiState.Error -> {
                Text(text = "Failed to load")
            }

        }
    }
}


@Composable
fun SentRequestsItem(
    modifier: Modifier = Modifier,
    user: User,
    contactRequestStatus: ContactRequestStatus,
    onWithdrawRequest: (String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UserHeader(
            user = user,
            contactRequestStatus = contactRequestStatus,
            statusType = UserHeaderStatusType.REQUEST
        )
        Button(
            onClick = { onWithdrawRequest(user.uid) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(R.string.withdraw))
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SentRequestsScreenPreview() {
    ChatEaseTheme() {
        Scaffold() {
            Column() {
                SentRequestsScreen(
                    onBackClick = {}
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.sent_requests

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.sent_requests.components.SentRequestsContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
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
        SentRequestsContent(
            paddingValues = paddingValues,
            sentRequests = sentRequests,
            onWithdrawRequest = sentRequestsViewModel::withdrawContactRequest
        )
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

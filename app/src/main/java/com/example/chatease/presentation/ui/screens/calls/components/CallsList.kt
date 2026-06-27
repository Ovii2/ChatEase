package com.example.chatease.presentation.ui.screens.calls.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.color
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.model.CallHistoryUiModel
import com.example.chatease.presentation.ui.screens.shared.calls.CallDirectionIcon
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toChatDateLabel
import com.example.chatease.utils.toFormattedCallHistoryTime

@Composable
fun CallsList(
    modifier: Modifier = Modifier,
    callHistoryUiModels: List<CallHistoryUiModel>
) {
    val context = LocalContext.current

    val groupedCalls = callHistoryUiModels
        .sortedByDescending { it.callHistory.timestamp }
        .groupBy { it.callHistory.timestamp.toChatDateLabel(context) }

    if (callHistoryUiModels.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_calls),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedCalls.forEach { (dateLabel, callsForDate) ->
                item {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.W600
                    )
                }

                items(callsForDate) { callHistoryUiModel ->
                    CallsListItem(
                        callHistoryUiModel = callHistoryUiModel
                    )
                }
            }
        }
    }
}

@Composable
fun CallsListItem(
    modifier: Modifier = Modifier,
    callHistoryUiModel: CallHistoryUiModel
) {
    val user = callHistoryUiModel.user
    val callDirection = callHistoryUiModel.callDirection
    val callHistory = callHistoryUiModel.callHistory

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                user = user,
                avatarSize = 45.dp,
                initialsFontSize = 20.sp,
                showStatus = false
            )
            Column(modifier = Modifier.widthIn(max = 250.dp)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(callDirection.toScreenName()),
                        color = callDirection.color(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.W500
                    )
                    if (callDirection != CallDirection.MISSED) {
                        Text(
                            text = callHistory.callDuration?.toFormattedCallHistoryTime() ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            }
        }
        CallDirectionIcon(callDirection = callDirection)
    }
}

@Preview(showBackground = true, showSystemUi = true,
         uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CallsListPreview() {
    val now = System.currentTimeMillis()
    val user = User(
        uid = "1",
        fullName = "Test Testtttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )

    val callHistoryUiModels = List(10) { index ->
        CallHistoryUiModel(
            callHistory = CallHistory(
                id = index.toString(),
                timestamp = now - listOf(
                    5 * 60 * 1000L,
                    30 * 60 * 1000L,
                    2 * 60 * 60 * 1000L,
                    24 * 60 * 60 * 1000L
                ).random(),
                callerId = "user_1",
                receiverId = "user_2",
                callDuration = (1000L..75_000L).random(),
                callType = CallType.AUDIO
            ),
            user = user,
            callDirection = listOf(
                CallDirection.MISSED,
                CallDirection.INCOMING,
                CallDirection.OUTGOING
            ).random(),
        )
    }

    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CallsList(
                    callHistoryUiModels = callHistoryUiModels
                )
            }
        }
    }
}

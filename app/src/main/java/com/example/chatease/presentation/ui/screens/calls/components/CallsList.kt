package com.example.chatease.presentation.ui.screens.calls.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.automirrored.filled.PhoneMissed
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
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
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.color
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toChatDateLabel
import com.example.chatease.utils.toFormattedTime

@Composable
fun CallsList(
    modifier: Modifier = Modifier,
    user: User,
    calls: List<Call>
) {
    val context = LocalContext.current

    val groupedCalls = calls
        .sortedByDescending { it.timestamp }
        .groupBy { call ->
            call.timestamp.toChatDateLabel(context)
        }
    if (calls.isEmpty()) {
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

                items(callsForDate) { call ->
                    CallsListItem(
                        user = user,
                        call = call,
                    )
                }
            }
        }
    }
}

@Composable
fun CallsListItem(
    modifier: Modifier = Modifier,
    user: User,
    call: Call
) {
    val icon = when (call.callDirection) {
        CallDirection.MISSED -> Icons.AutoMirrored.Filled.PhoneMissed
        CallDirection.INCOMING -> Icons.AutoMirrored.Filled.PhoneCallback
        CallDirection.OUTGOING -> Icons.Filled.Call
    }
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
                        text = stringResource(call.callDirection.toScreenName()),
                        color = call.callDirection.color(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.W500
                    )
                    if (call.callDirection != CallDirection.MISSED) {
                        Text(
                            text = call.callDuration?.toFormattedTime() ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            }
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = call.callDirection.color()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
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
    val calls = List(10) {
        Call(
            id = it.toString(),
            callDirection = listOf(
                CallDirection.MISSED,
                CallDirection.INCOMING,
                CallDirection.OUTGOING
            ).random(),
            timestamp = now - listOf(
                5 * 60 * 1000L,
                30 * 60 * 1000L,
                2 * 60 * 60 * 1000L,
                24 * 60 * 60 * 1000L,
                2 * 24 * 60 * 60 * 1000L,
                3 * 24 * 60 * 60 * 1000L,
                7 * 24 * 60 * 60 * 1000L,
            ).random(),
            userId = "user_1",
            callDuration = listOf(
                15_000L,
                42_000L,
                75_000L,
                180_000L,
                420_000L,
                840_000L
            ).random(),
            callType = CallType.AUDIO,
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
                    user = user,
                    calls = calls
                )
            }
        }
    }
}
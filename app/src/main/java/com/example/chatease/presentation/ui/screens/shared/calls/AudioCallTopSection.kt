package com.example.chatease.presentation.ui.screens.shared.calls

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

@Composable
fun AudioCallTopSection(
    modifier: Modifier = Modifier,
    callStatus: CallStatus,
    user: User,
    minutes: Int = 0,
    seconds: Int = 0
) {
    val statusColor = if (isSystemInDarkTheme()) successGreenDark else successGreenLight

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = callStatus.toScreenName(minutes, seconds),
                style = MaterialTheme.typography.bodyLarge,
                color = statusColor,
                fontWeight = FontWeight.W600
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (callStatus != CallStatus.INCOMING) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Call,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.audio_call),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }
        UserAvatar(
            user = user,
            avatarSize = 180.dp,
            initialsFontSize = 100.sp,
            showStatus = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AudioCallTopSectionPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AudioCallTopSection(
                    callStatus = CallStatus.CALLING,
                    user = user,
                    minutes = 3,
                    seconds = 23
                )
            }
        }
    }
}
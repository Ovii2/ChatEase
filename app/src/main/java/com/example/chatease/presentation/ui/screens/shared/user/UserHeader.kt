package com.example.chatease.presentation.ui.screens.shared.user

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.UserHeaderStatusType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

@Composable
fun UserHeader(
    modifier: Modifier = Modifier,
    user: User,
    avatarSize: Dp = 60.dp,
    statusBubbleSize: Dp = 18.dp,
    initialsFontSize: TextUnit = 24.sp,
    statusBubbleOffsetX: Dp = 1.dp,
    statusBubbleOffsetY: Dp = (-1).dp,
    contactRequestStatus: ContactRequestStatus,
    statusType: UserHeaderStatusType = UserHeaderStatusType.PRESENCE
) {
    val requestStatus = when (contactRequestStatus) {
        ContactRequestStatus.PENDING -> R.string.pending
        ContactRequestStatus.ACCEPTED -> R.string.accepted
        ContactRequestStatus.DECLINED -> R.string.declined
    }

    val requestStatusColor = when (contactRequestStatus) {
        ContactRequestStatus.ACCEPTED -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight
        ContactRequestStatus.DECLINED -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        ContactRequestStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    val presenceStatus = when (user.status) {
        UserPresenceStatus.ONLINE -> R.string.online
        UserPresenceStatus.AWAY -> R.string.away
        else -> R.string.offline
    }

    val statusText = when (statusType) {
        UserHeaderStatusType.NONE -> null

        UserHeaderStatusType.PRESENCE -> {
            stringResource(presenceStatus)
        }

        UserHeaderStatusType.REQUEST -> {
            stringResource(requestStatus)
        }
    }

    val statusColor = when (statusType) {
        UserHeaderStatusType.NONE -> Color.Transparent

        UserHeaderStatusType.PRESENCE -> {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        UserHeaderStatusType.REQUEST -> {
            requestStatusColor
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            user = user,
            avatarSize = avatarSize,
            statusBubbleSize = statusBubbleSize,
            initialsFontSize = initialsFontSize,
            statusBubbleOffsetX = statusBubbleOffsetX,
            statusBubbleOffsetY = statusBubbleOffsetY,
            showStatus = statusType == UserHeaderStatusType.PRESENCE
        )
        Column(modifier = Modifier.widthIn(max = 200.dp)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (statusText != null) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun UserHeaderPreview() {
    val user = User(
        uid = "1",
        fullName = "Test Testttttttttttttttttttttttttttttttttttttttttttttttttt",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    ChatEaseTheme() {
        Scaffold() {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                UserHeader(
                    user = user,
                    contactRequestStatus = ContactRequestStatus.PENDING,
                    statusType = UserHeaderStatusType.PRESENCE
                )
            }
        }
    }
}
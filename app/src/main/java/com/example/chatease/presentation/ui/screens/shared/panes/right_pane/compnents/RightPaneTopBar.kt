package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.awayYellow
import com.example.chatease.presentation.ui.theme.awayYellowDark
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RightPaneTopBar(
    modifier: Modifier = Modifier,
    user: User,
    onBackClick: () -> Unit
) {
    val userPresenceStatus = when (user.status) {
        UserPresenceStatus.ONLINE -> R.string.online
        UserPresenceStatus.AWAY -> R.string.away
        else -> R.string.offline
    }

    val statusColor = when (user.status) {
        UserPresenceStatus.ONLINE -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight
        UserPresenceStatus.AWAY -> if (isSystemInDarkTheme()) awayYellowDark else awayYellow
        UserPresenceStatus.OFFLINE -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    TopAppBar(
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(
                        user = user,
                        avatarSize = 44.dp,
                        statusBubbleSize = 14.dp,
                        initialsFontSize = 20.sp,
                        statusBubbleOffsetX = 3.dp,
                        statusBubbleOffsetY = (-1).dp
                    )
                    Column(modifier = Modifier.widthIn(max = 170.dp)) {
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(userPresenceStatus),
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = null
                    )
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.ic_video_cam),
                        contentDescription = null
                    )
                    Icon(
                        modifier = Modifier.size(26.dp),
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null
                    )
                }
            }
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable { onBackClick() },
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun RightPaneTopBarPreview() {
    val user = User(
        uid = "1",
        fullName = "Test test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    ChatEaseTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RightPaneTopBar(
                    user = user,
                    onBackClick = {},
                )
            }
        }
    }
}

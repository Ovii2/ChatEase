package com.example.chatease.presentation.screens.contacts.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.awayYellow
import com.example.chatease.presentation.ui.theme.awayYellowDark
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

@Composable
fun MyContactsSection(
    modifier: Modifier = Modifier,
    users: List<User>,
    onContactClick: (String) -> Unit
) {
    val groupedUsers = users
        .sortedBy { it.fullName }
        .groupBy { it.fullName.first().uppercase() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.my_contacts),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold

        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            groupedUsers.forEach { (letter, usersInGroup) ->
                item {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(usersInGroup) { user ->
                    MyContactsItem(
                        user = user,
                        onContactClick = onContactClick,
                    )
                }
            }
        }
    }
}


@Composable
fun MyContactsItem(
    modifier: Modifier = Modifier,
    user: User,
    onContactClick: (String) -> Unit
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onContactClick(user.uid) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(
                user = user,
                avatarSize = 50.dp,
                initialsFontSize = 20.sp,
                statusBubbleSize = 16.dp
            )
            Column(modifier = Modifier.widthIn(max = 250.dp)) {
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
        Icon(
            modifier = modifier.rotate(180f),
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = null
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MyContactsSectionPreview() {
    val users = List(8) {
        User(
            uid = "",
            fullName = "Test Testingggggggggggggggggggggggggggggggggg",
            email = "test@email.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE
        )
    }

    ChatEaseTheme() {
        Scaffold() {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyContactsSection(
                    users = users,
                    onContactClick = {},
                )
            }
        }
    }
}
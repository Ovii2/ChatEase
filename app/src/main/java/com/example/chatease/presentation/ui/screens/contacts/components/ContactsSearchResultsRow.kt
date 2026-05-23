package com.example.chatease.presentation.ui.screens.contacts.components

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.CooldownUiModel
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toFormattedTime
import kotlinx.coroutines.delay

@Composable
fun ContactsSearchResultsRow(
    users: List<User>,
    onAddContactClick: (String) -> Unit,
    currentUserId: String,
    sentRequests: List<String>,
    cooldownRequests: List<CooldownUiModel>
) {
    var currentTime by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(users) { user ->

            val isInvitationSent = user.uid in sentRequests
            val cooldown = cooldownRequests.find {
                it.userId == user.uid
            }
            val isCooldownActive = cooldown != null
            val remainingCooldownTime = cooldown?.expiresAt?.minus(currentTime) ?: 0L

            LaunchedEffect(isCooldownActive) {
                if (isCooldownActive) {
                    while (true) {
                        currentTime = System.currentTimeMillis()
                        delay(1000)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.widthIn(max = 200.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserAvatar(
                        user = user,
                        avatarSize = 46.dp,
                        initialsFontSize = 18.sp,
                        showStatus = false
                    )
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (currentUserId != user.uid) {
                    Button(
                        enabled = !isInvitationSent && !isCooldownActive,
                        onClick = { onAddContactClick(user.uid) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = when {
                                isInvitationSent -> stringResource(R.string.invite_sent)
                                isCooldownActive -> remainingCooldownTime.toFormattedTime()
                                else -> stringResource(R.string.add)
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ContactsSearchResultsRowPreview() {
    val users = List(5) {
        User(
            uid = it.toString(),
            fullName = "Test Testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE
        )
        User(
            uid = it.toString(),
            fullName = "Test Senior",
            email = "test@senior.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE
        )
        User(
            uid = it.toString(),
            fullName = "Test Tester",
            email = "test@tester.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE
        )
    }
    ChatEaseTheme() {
        Scaffold() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                ContactsSearchResultsRow(
                    users = users,
                    onAddContactClick = {},
                    currentUserId = "1",
                    sentRequests = listOf("1", "3", "4"),
                    cooldownRequests = listOf(),
                )
            }
        }
    }
}
package com.example.chatease.presentation.ui.screens.other_user_profile.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun OtherUserProfileTopSection(
    modifier: Modifier = Modifier,
    user: User,
    isConnected: Boolean,
    onSendRequest: () -> Unit,
    onSendMessage: () -> Unit,
    isBlocked: Boolean,
    onUnblockClick: (String) -> Unit
) {
    val buttonIcon = if (!isConnected) Icons.Outlined.Add else Icons.AutoMirrored.Default.Message
    val buttonText =
        if (!isConnected) stringResource(R.string.send_request) else stringResource(R.string.message)
    val buttonColor =
        if (!isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val buttonTextColor =
        if (!isConnected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface


    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(
            user = user,
            avatarSize = 140.dp,
            statusBubbleSize = 35.dp,
            initialsFontSize = 70.sp,
            statusBubbleOffsetX = (-2).dp,
            statusBubbleOffsetY = (-4).dp,
            showStatus = isConnected && !isBlocked
        )
        if (!isConnected && !isBlocked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.not_connected),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        if (!isBlocked) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = if (!isConnected) onSendRequest else onSendMessage,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                border = if (isConnected) BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                ) else null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = buttonIcon,
                        contentDescription = null
                    )
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        } else {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    ),
                onClick = { onUnblockClick(user.uid) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = stringResource(R.string.unblock),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OtherUserProfileTopSectionPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileTopSection(
                    user = user,
                    isConnected = false,
                    onSendRequest = {},
                    onSendMessage = {},
                    isBlocked = true,
                    onUnblockClick = {},
                )
            }
        }
    }
}

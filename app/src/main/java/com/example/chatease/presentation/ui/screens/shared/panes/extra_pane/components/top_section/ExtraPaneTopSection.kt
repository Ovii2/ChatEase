package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.top_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.textColor
import com.example.chatease.domain.model.enums.toScreenName
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPaneTopSection(
    modifier: Modifier = Modifier,
    user: User,
    showStatus: Boolean,
    showQuickActions: Boolean
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAvatar(
                modifier = Modifier.padding(vertical = 16.dp),
                user = user,
                avatarSize = 140.dp,
                statusBubbleSize = 35.dp,
                statusBubbleOffsetX = (-3).dp,
                statusBubbleOffsetY = (-5).dp,
                initialsFontSize = 70.sp,
                showStatus = showStatus
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showStatus) {
                Text(
                    text = user.status.toScreenName(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.W500,
                    color = user.status.textColor()
                )
            }
        }
        if (showQuickActions) {
            TopSectionQuickActionsRow(
                onAudioClick = {},
                onVideoClick = {},
                onSearchClick = {},
                onMoreClick = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExtraPaneTopSectionPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            ExtraPaneTopSection(
                modifier = Modifier.padding(paddingValues),
                user = user,
                showStatus = true,
                showQuickActions = true,
            )
        }
    }
}

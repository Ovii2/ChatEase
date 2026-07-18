package com.example.chatease.presentation.ui.screens.group_chat_info.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupChatInfoDetailsSection(
    modifier: Modifier = Modifier,
    membersCount: List<User>
) {
    val borderWidth = 1.dp
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            GroupChatInfoDetailsItem(
                title = R.string.about,
                label = R.string.about_label,
                borderWidth = borderWidth
            )
            GroupChatInfoDetailsItem(
                title = R.string.notifications,
                label = R.string.notifications_label,
                borderWidth = borderWidth
            )
            GroupChatInfoDetailsItem(
                title = R.string.media_links_more,
                showDivider = false,
                count = 30,
                borderWidth = borderWidth,
            )
        }
        Column(
            modifier = Modifier.border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            GroupChatInfoDetailsItem(
                title = R.string.members,
                showDivider = false,
                count = membersCount.size,
                borderWidth = borderWidth,
            )
        }
    }
}

@Composable
fun GroupChatInfoDetailsItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes label: Int? = null,
    showDivider: Boolean = true,
    count: Int? = null,
    borderWidth: Dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W500
            )
            label?.let {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } ?: ""
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            count?.let {
                Text(
                    text = "%,d".format(count),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            } ?: ""
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = borderWidth,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatInfoDetailsSectionPreview() {
    val members = List(12) {
        User(
            uid = it.toString(),
            fullName = "",
            email = "",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList()
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatInfoDetailsSection(
                    membersCount = members
                )
            }
        }
    }
}
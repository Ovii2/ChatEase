package com.example.chatease.presentation.screens.new_chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.chatease.presentation.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AllContactsSection(
    modifier: Modifier = Modifier,
    users: List<User>,
    count: Int,
    selected: Boolean,
    onChecked: () -> Unit,
    onStartChatClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.all_contacts),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                thickness = 0.75.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(users) { user ->
                    AllContactsItem(
                        user = user,
                        selected = selected,
                        onChecked = onChecked
                    )
                }
            }
        }
        Button(
            modifier = Modifier
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            onClick = onStartChatClick
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.next))
                if (count > 0) Text(text = "($count)") else Unit
            }
        }
    }
}

@Composable
fun AllContactsItem(
    modifier: Modifier = Modifier,
    user: User,
    selected: Boolean,
    onChecked: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(
                user = user,
                avatarSize = 40.dp,
                statusBubbleSize = 14.dp,
                initialsFontSize = 18.sp,
                statusBubbleOffsetX = 1.dp,
                statusBubbleOffsetY = 2.dp
            )
            Text(
                modifier = Modifier.widthIn(max = 200.dp),
                text = user.fullName,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        CircleCheckBox(
            selected = selected,
            onChecked = onChecked
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AllContactsSectionPreview() {
    val users = listOf(
        User(
            uid = "",
            fullName = "Test Testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE
        )
    )
    ChatEaseTheme() {
        Scaffold() { paddingValues ->
            AllContactsSection(
                modifier = Modifier.padding(paddingValues),
                users = users,
                count = 3,
                selected = false,
                onChecked = {},
                onStartChatClick = {}
            )
        }
    }
}

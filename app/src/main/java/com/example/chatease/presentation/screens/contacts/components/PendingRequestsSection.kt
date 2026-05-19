package com.example.chatease.presentation.screens.contacts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.presentation.screens.components.chat.UserAvatar

@Composable
fun PendingRequestsSection(
    modifier: Modifier = Modifier,
    onViewAllRequests: () -> Unit,
    users: List<User>,
    onCloseRequestClick: () -> Unit,
    onAcceptRequestClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewAllRequests() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.pending_requests),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(users) { index, user ->
                PendingRequestItem(
                    user = user,
                    onCloseRequestClick = onCloseRequestClick,
                    onAcceptRequestClick = onAcceptRequestClick
                )
            }
        }
    }
}

@Composable
fun PendingRequestItem(
    modifier: Modifier = Modifier,
    user: User,
    onCloseRequestClick: () -> Unit,
    onAcceptRequestClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.widthIn(max = 250.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                user = user,
                avatarSize = 50.dp,
                initialsFontSize = 20.sp,
                showStatus = false
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PendingItemRequestButton(
                onClick = onCloseRequestClick,
                icon = Icons.Default.Close,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PendingItemRequestButton(
                onClick = onAcceptRequestClick,
                icon = Icons.Default.Check,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
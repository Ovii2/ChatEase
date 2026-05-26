package com.example.chatease.presentation.ui.screens.contacts.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.PendingRequestActionState
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.example.chatease.presentation.ui.screens.shared.chat.UserAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlinx.coroutines.delay

@Composable
fun PendingRequestsSection(
    modifier: Modifier = Modifier,
    onViewAllRequests: () -> Unit,
    pendingRequests: List<PendingRequestUiModel>,
    onDismissRequestClick: () -> Unit,
    onAcceptRequestClick: (String) -> Unit,
    pendingRequestsCount: Int,
    pendingRequestLimit: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onViewAllRequests() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.pending_requests),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .defaultMinSize(
                            minWidth = 26.dp,
                            minHeight = 26.dp
                        )
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (pendingRequestsCount > 99) "99+" else "$pendingRequestsCount",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            if (pendingRequestsCount > pendingRequestLimit) {
                Text(
                    text = stringResource(R.string.view_all),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(
                items = pendingRequests,
                key = { it.requestId }) { pendingRequest ->
                PendingRequestItem(
                    pendingRequest = pendingRequest,
                    onDismissRequestClick = onDismissRequestClick,
                    onAcceptRequestClick = onAcceptRequestClick
                )
            }
        }
    }
}

@Composable
fun PendingRequestItem(
    modifier: Modifier = Modifier,
    pendingRequest: PendingRequestUiModel,
    onDismissRequestClick: () -> Unit,
    onAcceptRequestClick: (String) -> Unit
) {
    var actionState by rememberSaveable {
        mutableStateOf(PendingRequestActionState.IDLE)
    }

    LaunchedEffect(actionState) {
        if (actionState == PendingRequestActionState.ACCEPTED) {
            delay(900)
            onAcceptRequestClick(pendingRequest.requestId)
        }
    }

    AnimatedContent(
        targetState = actionState,
        transitionSpec = {
            when (targetState) {
                PendingRequestActionState.ACCEPTED -> {
                    fadeIn() togetherWith scaleOut()
                }

                PendingRequestActionState.DISMISSED -> {
                    slideInVertically() togetherWith slideOutHorizontally()
                }

                else -> {
                    fadeIn() togetherWith fadeOut()
                }
            }
        },
        label = "State"
    ) { state ->
        when (state) {
            PendingRequestActionState.ACCEPTED -> {
                Text(
                    text = stringResource(R.string.request_accepted),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            PendingRequestActionState.DISMISSED -> {
                Text(
                    text = stringResource(R.string.request_declined),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> {
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
                            user = pendingRequest.user,
                            avatarSize = 50.dp,
                            initialsFontSize = 20.sp,
                            showStatus = false
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = pendingRequest.user.fullName,
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
                            onClick = onDismissRequestClick,
                            icon = Icons.Default.Close,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        PendingItemRequestButton(
                            onClick = { actionState = PendingRequestActionState.ACCEPTED },
                            icon = Icons.Default.Check,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            iconColor = MaterialTheme.colorScheme.surface
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
private fun PendingRequestsSectionPreview() {
    val user = User(
        uid = "",
        fullName = "Test Testing",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )

    val pendingRequests = List(5) {
        PendingRequestUiModel(
            requestId = "1",
            user = user
        )
    }
    ChatEaseTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                PendingRequestsSection(
                    onViewAllRequests = {},
                    pendingRequests = pendingRequests,
                    onDismissRequestClick = {},
                    onAcceptRequestClick = {},
                    pendingRequestsCount = 323,
                    pendingRequestLimit = 3,
                )
            }
        }
    }
}
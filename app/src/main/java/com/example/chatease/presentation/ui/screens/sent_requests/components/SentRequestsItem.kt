package com.example.chatease.presentation.ui.screens.sent_requests.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.AlertDialogType
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.UserHeaderStatusType
import com.example.chatease.presentation.ui.screens.shared.chat.CommonAlertDialog
import com.example.chatease.presentation.ui.screens.shared.user.UserHeader
import kotlinx.coroutines.delay

@Composable
fun SentRequestsItem(
    modifier: Modifier = Modifier,
    user: User,
    contactRequestStatus: ContactRequestStatus,
    onWithdrawRequest: (String) -> Unit
) {
    var isWithdrawn by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isWithdrawn) {
        if (isWithdrawn) {
            delay(900)
            onWithdrawRequest(user.uid)
        }
    }

    AnimatedContent(
        targetState = isWithdrawn,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(400)
            ) + scaleIn() togetherWith fadeOut(
                animationSpec = tween(400)
            ) + scaleOut()
        },
        label = "Withdraw state"
    ) { withdrawn ->
        if (withdrawn) {
            Text(
                text = stringResource(R.string.request_withdrawn),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserHeader(
                    user = user,
                    contactRequestStatus = contactRequestStatus,
                    statusType = UserHeaderStatusType.REQUEST
                )
                Button(
                    onClick = {
                        showDialog = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = stringResource(R.string.withdraw))
                }
            }
        }
    }
    if (showDialog) {
        CommonAlertDialog(
            title = R.string.confirm_withdraw_request_title,
            bodyText = R.string.confirm_withdraw_request_body,
            bodyTextParam = 24,
            dismissButtonText = R.string.dismiss_btn,
            acceptButtonText = R.string.withdraw_btn,
            onDismiss = { showDialog = false },
            onAccept = {
                showDialog = false
                isWithdrawn = true
            },
            alertDialogType = AlertDialogType.CONFIRMATION
        )
    }
}

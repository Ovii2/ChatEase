package com.example.chatease.presentation.ui.screens.shared.chat

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.chatease.R
import com.example.chatease.domain.model.enums.AlertDialogType
import com.example.chatease.domain.model.enums.ButtonType
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.awayYellow
import com.example.chatease.presentation.ui.theme.awayYellowDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonAlertDialog(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes bodyText: Int,
    @StringRes dismissButtonText: Int,
    @StringRes acceptButtonText: Int,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    alertDialogType: AlertDialogType,
    bodyTextParam: Int? = null
) {

    val resolvedBodyText = bodyTextParam?.let {
        stringResource(bodyText, it)
    } ?: stringResource(bodyText)

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = DialogProperties(),
        content = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AlertDialogHeaderIcon(
                            alertDialogType = alertDialogType
                        )
                        Text(
                            text = stringResource(title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = resolvedBodyText,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        AlertDialogButton(
                            buttonText = dismissButtonText,
                            onClick = onDismiss,
                            buttonType = ButtonType.DISMISS,
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        AlertDialogButton(
                            buttonText = acceptButtonText,
                            onClick = onAccept,
                            buttonType = ButtonType.ACCEPT
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AlertDialogHeaderIcon(
    modifier: Modifier = Modifier,
    alertDialogType: AlertDialogType
) {
    val icon = when (alertDialogType) {
        AlertDialogType.WARNING -> Icons.Outlined.Warning
        AlertDialogType.CONFIRMATION -> Icons.AutoMirrored.Filled.HelpOutline
        AlertDialogType.INFORMATION -> Icons.Outlined.Info
        AlertDialogType.ERROR -> Icons.Default.ErrorOutline
    }

    val iconColor = when (alertDialogType) {
        AlertDialogType.CONFIRMATION,
        AlertDialogType.INFORMATION -> MaterialTheme.colorScheme.primary

        AlertDialogType.WARNING -> if (isSystemInDarkTheme()) awayYellowDark else awayYellow
        AlertDialogType.ERROR -> MaterialTheme.colorScheme.error
    }

    val backgroundColor = when (alertDialogType) {
        AlertDialogType.CONFIRMATION,
        AlertDialogType.INFORMATION -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

        AlertDialogType.WARNING -> if (isSystemInDarkTheme()) awayYellowDark.copy(alpha = 0.25f) else
            awayYellow.copy(alpha = 0.25f)

        AlertDialogType.ERROR -> MaterialTheme.colorScheme.error.copy(0.25f)

    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(65.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = icon,
            contentDescription = null,
            tint = iconColor
        )
    }
}

@Composable
fun AlertDialogButton(
    modifier: Modifier = Modifier,
    @StringRes buttonText: Int,
    onClick: () -> Unit,
    buttonType: ButtonType
) {
    val containerColor = if (buttonType == ButtonType.ACCEPT) MaterialTheme.colorScheme.primary else
        MaterialTheme.colorScheme.surface
    val contentColor = if (buttonType == ButtonType.ACCEPT) MaterialTheme.colorScheme.surface else
        MaterialTheme.colorScheme.primary

    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = stringResource(buttonText))
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CommonAlertDialogPreview() {
    ChatEaseTheme() {
        Scaffold() { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                CommonAlertDialog(
                    title = R.string.confirm_withdraw_request_title,
                    bodyText = R.string.confirm_withdraw_request_body,
                    dismissButtonText = R.string.dismiss_btn,
                    acceptButtonText = R.string.withdraw,
                    onDismiss = {},
                    onAccept = {},
                    alertDialogType = AlertDialogType.CONFIRMATION,
                    bodyTextParam = 24
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.privacy_security.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun PrivacyAndSecurityScreenContent(
    modifier: Modifier = Modifier,
    onNavigateToBlockedUsers: () -> Unit
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        PrivacyAndSecuritySectionLayout(
            title = R.string.privacy_controls
        ) {
            PrivacyAndSecurityItem(
                icon = Icons.Filled.Block,
                title = R.string.blocked_users,
                label = R.string.people_you_have_blocked,
                onClick = onNavigateToBlockedUsers
            )
            PrivacyAndSecurityItem(
                icon = Icons.Filled.Group,
                title = R.string.who_can_contact_me,
                label = R.string.manage_incoming_messages,
                onClick = {}
            )
            PrivacyAndSecurityItem(
                icon = Icons.Outlined.Circle,
                title = R.string.online_status,
                label = R.string.control_online_visibility,
                onClick = {}
            )
            PrivacyAndSecurityItem(
                icon = Icons.Filled.Visibility,
                title = R.string.read_receipts,
                label = R.string.control_read_receipts,
                onClick = {}
            )
            PrivacyAndSecurityItem(
                icon = Icons.Filled.AccountCircle,
                title = R.string.profile_visibility,
                label = R.string.control_who_sees_info,
                onClick = {}
            )
        }
        PrivacyAndSecuritySectionLayout(
            title = R.string.security
        ) {
            PrivacyAndSecurityItem(
                icon = Icons.Filled.Lock,
                title = R.string.app_lock,
                label = R.string.lock_app_with_pin_or,
                onClick = {}
            )
            PrivacyAndSecurityItem(
                icon = Icons.Outlined.Shield,
                title = R.string.two_step,
                label = R.string.extra_security_layer,
                onClick = {}
            )
            PrivacyAndSecurityItem(
                icon = Icons.Filled.Monitor,
                title = R.string.active_sessions,
                label = R.string.manage_active_sessions,
                onClick = {}
            )
        }
    }
}

@Composable
fun PrivacyAndSecurityItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes title: Int,
    @StringRes label: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column() {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PrivacyAndSecurityScreenContentPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrivacyAndSecurityScreenContent(
                    onNavigateToBlockedUsers = {}
                )
            }
        }
    }
}
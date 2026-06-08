package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.notifications_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.section_container.SectionContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPaneNotificationsSection(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconSize: Dp
) {
    SectionContainer(
        sectionTitle = R.string.notifications,
        content = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = if (checked) Icons.Outlined.NotificationsOff else Icons.Outlined.Notifications,
                        contentDescription = null
                    )
                    Text(
                        text = if (checked) stringResource(R.string.unmute_notifications) else stringResource(
                            R.string.mute_notifications
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Switch(
                    modifier = Modifier.scale(0.7f),
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExtraPaneNotificationsSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ExtraPaneNotificationsSection(
                    checked = false,
                    onCheckedChange = {},
                    iconSize = 26.dp,
                )
            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.shared.calls

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AudioCallActionSection(
    modifier: Modifier = Modifier,
    callStatus: CallStatus
) {
    var isMuted by rememberSaveable { mutableStateOf(false) }
    val muteIcon = if (isMuted) Icons.Default.Mic else Icons.Default.MicOff
    val muteIconLabel = if (isMuted) R.string.unmute else R.string.mute

    when (callStatus) {
        CallStatus.CALLING -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AudioCallActionItem(
                    label = R.string.speaker,
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    onClick = {},
                )
                AudioCallActionItem(
                    label = muteIconLabel,
                    icon = muteIcon,
                    onClick = { isMuted = !isMuted },
                )
                AudioCallActionItem(
                    label = R.string.bluetooth,
                    icon = Icons.Default.Bluetooth,
                    onClick = {},
                )
            }
        }

        CallStatus.INCOMING -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AudioCallActionItem(
                    label = R.string.swipe_up_message,
                    icon = Icons.AutoMirrored.Filled.Message,
                    onClick = {},
                )
            }
        }

        CallStatus.CONNECTED -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AudioCallActionItem(
                        label = R.string.speaker,
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        onClick = {},
                    )
                    AudioCallActionItem(
                        label = muteIconLabel,
                        icon = muteIcon,
                        onClick = { isMuted = !isMuted },
                    )
                    AudioCallActionItem(
                        label = R.string.bluetooth,
                        icon = Icons.Default.Bluetooth,
                        onClick = {},
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AudioCallActionItem(
                        label = R.string.add_call,
                        icon = Icons.Default.PersonAddAlt,
                        onClick = {},
                    )
                    AudioCallActionItem(
                        label = R.string.keypad,
                        icon = Icons.Default.Dialpad,
                        onClick = { },
                    )
                    AudioCallActionItem(
                        label = R.string.more,
                        icon = Icons.Default.MoreHoriz,
                        onClick = { },
                    )
                }
            }
        }

        else -> Unit
    }
}

@Composable
fun AudioCallActionItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    @StringRes label: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = CircleShape
                )
                .size(65.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = icon,
                contentDescription = null
            )
        }
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AudioCallActionSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AudioCallActionSection(
                    callStatus = CallStatus.CONNECTED
                )
            }
        }
    }
}

package com.example.chatease.data.local.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonAddAlt
import com.example.chatease.R
import com.example.chatease.domain.model.AudioCallActionItem

object AudioCallActionsDataSource {

    val callingActionItems = listOf(
        AudioCallActionItem(
            label = R.string.speaker,
            icon = Icons.AutoMirrored.Filled.VolumeUp
        ),
        AudioCallActionItem(
            label = R.string.mute,
            icon = Icons.Default.MicOff
        ),
        AudioCallActionItem(
            label = R.string.bluetooth,
            icon = Icons.Default.Bluetooth
        )
    )

    val activeCallActions = listOf(
        AudioCallActionItem(
            label = R.string.speaker,
            icon = Icons.AutoMirrored.Filled.VolumeUp
        ),
        AudioCallActionItem(
            label = R.string.mute,
            icon = Icons.Default.MicOff
        ),
        AudioCallActionItem(
            label = R.string.bluetooth,
            icon = Icons.Default.Bluetooth
        ),
        AudioCallActionItem(
            label = R.string.add_call,
            icon = Icons.Default.PersonAddAlt
        ),
        AudioCallActionItem(
            label = R.string.keypad,
            icon = Icons.Default.Dialpad
        ),
        AudioCallActionItem(
            label = R.string.more,
            icon = Icons.Default.MoreHoriz
        )
    )

    val incomingCallActions = listOf(
        AudioCallActionItem(
            label = R.string.swipe_up_message,
            icon = Icons.AutoMirrored.Filled.Message
        )
    )

}
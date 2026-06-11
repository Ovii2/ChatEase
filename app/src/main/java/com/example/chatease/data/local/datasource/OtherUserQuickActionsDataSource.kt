package com.example.chatease.data.local.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.OutlinedFlag
import com.example.chatease.R
import com.example.chatease.domain.model.QuickActionsItem

object OtherUserQuickActionsDataSource {
    val actions = listOf(
        QuickActionsItem(
            icon = Icons.Outlined.CameraAlt,
            label = R.string.view_photo
        ),
        QuickActionsItem(
            icon = Icons.Outlined.Block,
            label = R.string.block
        ),
        QuickActionsItem(
            icon = Icons.Outlined.OutlinedFlag,
            label = R.string.report
        )
    )
}

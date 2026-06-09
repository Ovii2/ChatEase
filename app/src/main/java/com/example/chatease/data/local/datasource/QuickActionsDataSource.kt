package com.example.chatease.data.local.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Videocam
import com.example.chatease.R
import com.example.chatease.domain.model.QuickActionsItem

object QuickActionsDataSource {

    val actions = listOf(
        QuickActionsItem(
            image = R.drawable.ic_phone,
            label = R.string.audio
        ),
        QuickActionsItem(
            image = R.drawable.ic_video_cam,
            label = R.string.video
        ),
        QuickActionsItem(
            icon = Icons.Outlined.Search,
            label = R.string.search
        ),
        QuickActionsItem(
            icon = Icons.Outlined.MoreHoriz,
            label = R.string.more
        )
    )
}
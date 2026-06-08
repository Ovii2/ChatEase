package com.example.chatease.data.local.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import com.example.chatease.R
import com.example.chatease.domain.model.ContactActionItem

object ContactActionsDataSource {
    val actions = listOf(
        ContactActionItem(
            label = R.string.view_contact,
            icon = Icons.Outlined.Person,
            isDestructive = false
        ),
        ContactActionItem(
            label = R.string.share_contact,
            icon = Icons.Outlined.Share,
            isDestructive = false
        ),
        ContactActionItem(
            label = R.string.block_contact,
            icon = Icons.Outlined.Block,
            isDestructive = false
        ),
        ContactActionItem(
            label = R.string.delete_chat,
            icon = Icons.Outlined.Delete,
            isDestructive = true
        ),
    )
}
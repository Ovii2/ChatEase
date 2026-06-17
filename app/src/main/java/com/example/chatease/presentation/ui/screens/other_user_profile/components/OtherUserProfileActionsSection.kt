package com.example.chatease.presentation.ui.screens.other_user_profile.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.data.local.datasource.OtherUserQuickActionsDataSource
import com.example.chatease.domain.model.QuickActionsItem
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun OtherUserProfileActionsSection(
    modifier: Modifier = Modifier,
    onViewPhotoClick: () -> Unit,
    onBlockClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val actions = OtherUserQuickActionsDataSource.actions
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        actions.forEach { item ->
            val onClick = when (item.label) {
                R.string.view_photo -> onViewPhotoClick
                R.string.block -> onBlockClick
                R.string.report -> onReportClick
                else -> {
                    {}
                }
            }
            OtherUserProfileActionItem(
                modifier = Modifier.weight(1f),
                item = item,
                onClick = onClick
            )
        }
    }
}

@Composable
fun OtherUserProfileActionItem(
    modifier: Modifier = Modifier,
    item: QuickActionsItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            item.icon?.let {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null
                )
            }
        }
        Text(
            text = stringResource(item.label),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtherUserProfileActionsSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtherUserProfileActionsSection(
                    onViewPhotoClick = {},
                    onBlockClick = {},
                    onReportClick = {}
                )

            }
        }
    }
}

package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.top_section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.data.local.datasource.QuickActionsDataSource
import com.example.chatease.domain.model.QuickActionsItem
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun TopSectionQuickActionsRow(
    modifier: Modifier = Modifier, onAudioClick: () -> Unit,
    onVideoClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val items = QuickActionsDataSource.actions
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val onClick = when (item.label) {
                R.string.audio -> onAudioClick
                R.string.video -> onVideoClick
                R.string.search -> onSearchClick
                R.string.more -> onMoreClick
                else -> {
                    {}
                }
            }

            TopSectionQuickActionItem(
                item = item,
                onClick = onClick
            )
        }
    }
}

@Composable
fun TopSectionQuickActionItem(
    modifier: Modifier = Modifier,
    item: QuickActionsItem,
    onClick: () -> Unit
) {
    val size = 24.dp

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(15.dp)
            )
            .size(80.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item.icon?.let {
                Icon(
                    modifier = Modifier.size(size),
                    imageVector = it,
                    contentDescription = stringResource(item.label)
                )
            } ?: item.image?.let {
                Icon(
                    modifier = Modifier.size(size),
                    painter = painterResource(it),
                    contentDescription = stringResource(item.label)
                )
            }
            Text(
                text = stringResource(item.label),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TopSectionQuickActionsRowPreview() {
    ChatEaseTheme {
        Scaffold() { paddingValues ->
            Row(modifier = Modifier.padding(paddingValues)) {
                TopSectionQuickActionsRow(
                    onAudioClick = {},
                    onVideoClick = {},
                    onSearchClick = {},
                    onMoreClick = {}
                )
            }
        }
    }
}
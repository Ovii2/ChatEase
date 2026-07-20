package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    @StringRes title: Int? = null,
    transparent: Boolean = false,
    actionIcon: ImageVector? = null,
    onActionIconClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            title?.let {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            } ?: ""
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable { onBackClick() },
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = null
            )
        },
        actions = {
            actionIcon?.let {
                Icon(
                    modifier = modifier.clickable { onActionIconClick() },
                    imageVector = actionIcon, contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (transparent) Color.Transparent else MaterialTheme.colorScheme.surface
        )
    )
}
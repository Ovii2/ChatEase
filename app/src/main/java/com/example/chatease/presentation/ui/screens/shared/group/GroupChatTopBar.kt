package com.example.chatease.presentation.ui.screens.shared.group

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    @StringRes title: Int? = null,
    actionIcon: ImageVector? = null,
    onActionIconClick: () -> Unit = {},
    expanded: Boolean,
    onDismiss: () -> Unit,
    onChangeGroupPhotoClick: () -> Unit
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
                    imageVector = actionIcon,
                    contentDescription = null
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismiss,
                    offset = DpOffset(
                        x = (-6).dp,
                        y = 2.dp
                    )
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.change_group_photo)) },
                        onClick = onChangeGroupPhotoClick
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatTopBarPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatTopBar(
                    onBackClick = {},
                    expanded = true,
                    onDismiss = {},
                    onChangeGroupPhotoClick = {},
                )
            }
        }
    }
}

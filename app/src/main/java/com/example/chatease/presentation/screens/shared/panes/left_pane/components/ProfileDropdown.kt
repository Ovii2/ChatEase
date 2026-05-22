package com.example.chatease.presentation.screens.shared.panes.left_pane.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.chatease.R

@Composable
fun ProfileDropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    offset: DpOffset = DpOffset(0.dp, 0.dp)
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = offset
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.view_profile)) },
            onClick = {
                onDismiss()
                onProfileClick()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.logout)) },
            onClick = {
                onDismiss()
                onLogoutClick()
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.error
            )
        )
    }
}
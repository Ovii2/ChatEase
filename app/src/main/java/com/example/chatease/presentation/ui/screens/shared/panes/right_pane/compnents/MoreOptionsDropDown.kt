package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

@Composable
fun MoreOptionsDropDown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onViewProfileClick: () -> Unit,
    onTogglePreviewClick: () -> Unit,
    onDeleteChatClick: () -> Unit,
    offset: DpOffset = DpOffset(0.dp, 0.dp)
) {
    var isPreviewEnabled by rememberSaveable { mutableStateOf(false) }
    val greenColor = if (isSystemInDarkTheme()) successGreenDark else successGreenLight
    val previewStatusColor = if (isPreviewEnabled) greenColor else MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
    val previewStatusText = if (isPreviewEnabled) stringResource(R.string.on) else stringResource(R.string.off)

    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = offset
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.view_profile)) },
            onClick = onViewProfileClick
        )
        DropdownMenuItem(
            text = {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(text = stringResource(R.string.preview))
                    Text(text = previewStatusText, color = previewStatusColor)
                }
            },
            onClick = {
                isPreviewEnabled = !isPreviewEnabled
                onTogglePreviewClick()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.delete_chat),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            },
            onClick = onDeleteChatClick
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MoreOptionsDropDownPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MoreOptionsDropDown(
                    expanded = true,
                    onDismiss = {},
                    onViewProfileClick = {},
                    onTogglePreviewClick = {},
                    onDeleteChatClick = {},
                )
            }
        }
    }
}
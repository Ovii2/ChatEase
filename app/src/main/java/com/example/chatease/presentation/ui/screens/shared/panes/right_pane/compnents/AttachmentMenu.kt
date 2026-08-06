package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AttachmentMenu(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    alignment: Alignment,
    onAddImageClick: () -> Unit,
    onAddFileClick: () -> Unit
) {
    Popup(
        alignment = alignment,
        offset = IntOffset(0, -170),
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            clippingEnabled = false
        )
    ) {
        Surface(
            modifier = modifier,
            shadowElevation = 3.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AttachmentMenuItem(
                    icon = Icons.Outlined.Image,
                    label = R.string.image,
                    onClick = {
                        onAddImageClick()
                        onDismiss()
                    },
                )
                AttachmentMenuItem(
                    icon = Icons.Outlined.AttachFile,
                    label = R.string.file,
                    onClick = {
                        onAddFileClick()
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
fun AttachmentMenuItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes label: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(text = stringResource(label))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AttachmentMenuPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AttachmentMenu(
                    onDismiss = {},
                    alignment = Alignment.BottomEnd,
                    onAddImageClick = {},
                    onAddFileClick = {},
                )
            }
        }
    }
}
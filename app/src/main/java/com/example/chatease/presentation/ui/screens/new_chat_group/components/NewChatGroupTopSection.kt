package com.example.chatease.presentation.ui.screens.new_chat_group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatGroupTopSection(
    modifier: Modifier = Modifier,
    groupName: String,
    onGroupNameTextChange: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = CircleShape
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Text(
                text = stringResource(R.string.add_photo),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = groupName,
            onValueChange = onGroupNameTextChange,
            placeholder = { Text(text = stringResource(R.string.group_name)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Text(
                    text = "0/50",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(15.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewChatGroupTopSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupTopSection(
                    groupName = "",
                    onGroupNameTextChange = {}
                )
            }
        }
    }
}
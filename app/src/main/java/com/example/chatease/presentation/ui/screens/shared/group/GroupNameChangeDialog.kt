package com.example.chatease.presentation.ui.screens.shared.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupNameChangeDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAccept: (String) -> Unit,
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    maxLength: Int = 50,
    isGroupNameError: Boolean
) {
    val trimmedGroupName = groupName.trim()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .height(250.dp)
                .fillMaxWidth(0.9f)
                .widthIn(min = 500.dp, max = 600.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.change_group_name),
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = groupName,
                    onValueChange = { if (it.length <= maxLength) onGroupNameChange(it) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface

                    ),
                    trailingIcon = {
                        Text(text = "${trimmedGroupName.length}/$maxLength")
                    },
                    maxLines = 2,
                    isError = isGroupNameError
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(text = stringResource(R.string.dismiss_btn))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = { onAccept(trimmedGroupName) },
                        enabled = !isGroupNameError && trimmedGroupName.length in 5..50
                    ) {
                        Text(text = stringResource(R.string.accept))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupNameChangeDialogPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupNameChangeDialog(
                    onDismiss = {},
                    onAccept = {},
                    groupName = "Test Group",
                    onGroupNameChange = {},
                    isGroupNameError = false,
                )
            }
        }
    }
}

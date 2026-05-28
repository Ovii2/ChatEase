package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.TagFaces
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MessageInputBar(
    modifier: Modifier = Modifier,
    onEmojiClick: () -> Unit,
    onMicrophoneClick: () -> Unit,
    onMoreOptionsClick: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        state = rememberTextFieldState(),
        placeholder = { Text(text = stringResource(R.string.type_a_message)) },
        shape = CircleShape,
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        trailingIcon = {
            Row(
                modifier = Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.clickable { onEmojiClick() },
                    imageVector = Icons.Outlined.TagFaces,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.clickable { onMicrophoneClick() },
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.clickable { onMoreOptionsClick() },
                    imageVector = Icons.Outlined.MoreVert, contentDescription = null
                )
            }
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MessageInputBarPreview() {
    ChatEaseTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            MessageInputBar(
                onEmojiClick = {},
                onMicrophoneClick = {},
                onMoreOptionsClick = {}
            )
        }
    }
}
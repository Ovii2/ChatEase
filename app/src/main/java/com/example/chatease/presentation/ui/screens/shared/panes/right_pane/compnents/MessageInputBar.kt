package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MessageInputBar(
    modifier: Modifier = Modifier,
    onMicrophoneClick: () -> Unit,
    onSendMessageClick: (String) -> Unit,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    isPeekEnabled: Boolean,
    onPeekClick: () -> Unit,
    onInputFocused: () -> Unit,
    isBlockedByOtherUser: Boolean,
    isUserGroupMember: Boolean
) {
    if (isBlockedByOtherUser || !isUserGroupMember) {
        val message = when {
            isBlockedByOtherUser -> R.string.cannot_reply
            else -> R.string.user_is_not_group_member
        }

        Box(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.primary)
                .height(56.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface,
            )
        }
    } else {
        OutlinedTextField(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onInputFocused()
                    }
                },
            value = messageText,
            onValueChange = onMessageTextChange,
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        modifier = Modifier.clickable {
                            onPeekClick()
                        },
                        imageVector = if (isPeekEnabled) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null
                    )
                    AnimatedContent(
                        targetState = messageText.isNotEmpty(),
                        transitionSpec = {
                            fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                        },
                    ) { hasText ->
                        if (hasText) {
                            Icon(
                                modifier = Modifier
                                    .clickable { onSendMessageClick(messageText) }
                                    .size(24.dp),
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                modifier = Modifier.clickable { onMicrophoneClick() },
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MessageInputBarPreview() {
    ChatEaseTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                MessageInputBar(
                    onMicrophoneClick = {},
                    onSendMessageClick = {},
                    messageText = "",
                    onMessageTextChange = {},
                    isPeekEnabled = false,
                    onPeekClick = {},
                    onInputFocused = {},
                    isBlockedByOtherUser = false,
                    isUserGroupMember = true,
                )
            }
        }
    }
}
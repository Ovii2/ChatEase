package com.example.chatease.presentation.ui.screens.new_chat_group.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NewChatGroupTopSection(
    modifier: Modifier = Modifier,
    groupName: String,
    onGroupNameTextChange: (String) -> Unit,
    isSuggestGroupNameVisible: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onAcceptGroupNameSuggestion: (String) -> Unit,
    onRefreshGroupNameSuggestion: () -> Unit,
    suggestedGroupName: String,
    groupNameError: Boolean
) {
    val checkColor = if (isSystemInDarkTheme()) successGreenDark else successGreenLight
    val bottomEndCornerRadius = if (isSuggestGroupNameVisible) 0.dp else 15.dp
    val maxSymbols = 50
    var isClicked by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isClicked) -180f else 0f)

    LaunchedEffect(isClicked) {
        delay(500.milliseconds)
        isClicked = false
    }

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

        Column() {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
                value = groupName,
                onValueChange = {
                    if (it.length <= maxSymbols) {
                        onGroupNameTextChange(it)
                    }
                },
                placeholder = { Text(text = stringResource(R.string.group_name)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Text(
                        text = "${groupName.length}/${maxSymbols}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp,
                    bottomEnd = bottomEndCornerRadius,
                    bottomStart = bottomEndCornerRadius
                ),
                isError = groupNameError,
                maxLines = 3
            )
            AnimatedVisibility(
                visible = isSuggestGroupNameVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut(animationSpec = snap()) + shrinkVertically(animationSpec = snap()),
                modifier = Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$suggestedGroupName ?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.clickable {
                                onAcceptGroupNameSuggestion(
                                    suggestedGroupName
                                )
                            },
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = checkColor
                        )
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    isClicked = true
                                    onRefreshGroupNameSuggestion()
                                }
                                .rotate(rotation),
                            imageVector = Icons.Filled.Replay,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
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
                    onGroupNameTextChange = {},
                    isSuggestGroupNameVisible = true,
                    onFocusChanged = {},
                    onAcceptGroupNameSuggestion = {},
                    onRefreshGroupNameSuggestion = {},
                    suggestedGroupName = "Test",
                    groupNameError = false,
                )
            }
        }
    }
}
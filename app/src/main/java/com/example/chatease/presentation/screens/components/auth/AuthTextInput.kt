package com.example.chatease.presentation.screens.components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AuthTextInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    error: Int?,
    showError: Boolean,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
        placeholder = placeholder,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),

            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),

            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorSupportingTextColor = MaterialTheme.colorScheme.error
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        isError = showError && error != null,
        supportingText = {
            if (showError && error != null) {
                Text(stringResource(error))
            }
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            AnimatedVisibility(visible = error == null && value.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.check_icon)
                )
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthTextInputPreview() {
    ChatEaseTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AuthTextInput(
                value = "Test",
                onValueChange = {},
                placeholder = { R.string.email },
                error = 1,
                showError = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                leadingIcon = Icons.Outlined.Person
            )
        }
    }
}
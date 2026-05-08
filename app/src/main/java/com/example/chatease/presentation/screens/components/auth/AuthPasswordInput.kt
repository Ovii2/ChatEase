package com.example.chatease.presentation.screens.components.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthPasswordInput(
    value: String,
    isPasswordVisible: Boolean,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    error: Int?,
    showError: Boolean,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onTogglePasswordVisibility: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        value = value,
        singleLine = true,
        visualTransformation =
            if (isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null) },
        onValueChange = onValueChange,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
        placeholder = placeholder,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),

            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                alpha = 0.3f
            ),
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
        }
    )
}
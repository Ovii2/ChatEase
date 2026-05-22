package com.example.chatease.presentation.ui.screens.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.shared.auth.AuthActionButton
import com.example.chatease.presentation.ui.screens.shared.auth.AuthPasswordInput
import com.example.chatease.presentation.ui.screens.shared.auth.AuthTextInput
import com.example.chatease.presentation.ui.state.LoginUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LoginScreenForm(
    emailValue: String,
    onEmailValueChange: (String) -> Unit,
    emailError: Int?,
    showEmailError: Boolean,
    emailKeyboardType: KeyboardType,
    emailImeAction: ImeAction,
    passwordValue: String,
    passWordVisible: Boolean,
    onPasswordValueChange: (String) -> Unit,
    passWordError: Int?,
    showPassWordError: Boolean,
    passwordImeAction: ImeAction,
    onTogglePasswordVisibility: () -> Unit,
    rememberMeChecked: Boolean,
    onRememberMeChecked: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    loginUiState: LoginUiState
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AuthTextInput(
            value = emailValue,
            onValueChange = onEmailValueChange,
            placeholder = { Text(text = stringResource(R.string.email)) },
            error = emailError,
            showError = showEmailError,
            keyboardType = emailKeyboardType,
            imeAction = emailImeAction,
            leadingIcon = Icons.Outlined.Person
        )
        AuthPasswordInput(
            value = passwordValue,
            isPasswordVisible = passWordVisible,
            onValueChange = onPasswordValueChange,
            placeholder = { Text(text = stringResource(R.string.password)) },
            error = passWordError,
            showError = showPassWordError,
            imeAction = passwordImeAction,
            onTogglePasswordVisibility = onTogglePasswordVisibility
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMeChecked,
                    onCheckedChange = onRememberMeChecked,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = stringResource(R.string.remember_me),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AuthActionButton(
            buttonText = R.string.login,
            isLoading = loginUiState is LoginUiState.Loading,
            isSuccess = loginUiState is LoginUiState.Success,
            onClick = onLoginClick,
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
            )
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenFormPreview() {
    ChatEaseTheme() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginScreenForm(
                emailValue = "email@email.com",
                onEmailValueChange = {},
                emailError = 1,
                showEmailError = false,
                emailKeyboardType = KeyboardType.Email,
                emailImeAction = ImeAction.Next,
                passwordValue = "",
                passWordVisible = false,
                onPasswordValueChange = {},
                passWordError = 1,
                showPassWordError = false,
                passwordImeAction = ImeAction.Done,
                onTogglePasswordVisibility = {},
                rememberMeChecked = true,
                onRememberMeChecked = {},
                onLoginClick = {},
                loginUiState = LoginUiState.Idle,
            )
        }
    }
}
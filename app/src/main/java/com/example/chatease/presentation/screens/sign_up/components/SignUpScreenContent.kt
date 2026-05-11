package com.example.chatease.presentation.screens.sign_up.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.chatease.presentation.screens.components.auth.AuthActionButton
import com.example.chatease.presentation.screens.components.auth.AuthPasswordInput
import com.example.chatease.presentation.screens.components.auth.AuthTextInput
import com.example.chatease.presentation.ui.state.SignUpUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    fullNameValue: String,
    onFullNameValueChange: (String) -> Unit,
    fullNameError: Int?,
    showFullNameError: Boolean,
    emailValue: String,
    onEmailFieldChange: (String) -> Unit,
    emailError: Int?,
    showEmailError: Boolean,
    passwordValue: String,
    onPasswordValueChange: (String) -> Unit,
    passwordError: Int?,
    showPasswordError: Boolean,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    confirmPasswordValue: String,
    onConfirmPasswordValueChange: (String) -> Unit,
    confirmPasswordError: Int?,
    showConfirmPasswordError: Boolean,
    onSignUpClick: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    signUpUiState: SignUpUiState
) {
    val appleLogo =
        if (isSystemInDarkTheme()) R.drawable.ic_apple_white else R.drawable.ic_apple_black

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = stringResource(R.string.join_chatease),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AuthTextInput(
                    value = fullNameValue,
                    onValueChange = onFullNameValueChange,
                    placeholder = { Text(text = stringResource(R.string.full_name)) },
                    error = fullNameError,
                    showError = showFullNameError,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    leadingIcon = Icons.Outlined.Person
                )
                AuthTextInput(
                    value = emailValue,
                    onValueChange = onEmailFieldChange,
                    placeholder = { Text(text = stringResource(R.string.email)) },
                    error = emailError,
                    showError = showEmailError,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    leadingIcon = Icons.Outlined.Email
                )
                AuthPasswordInput(
                    value = passwordValue,
                    isPasswordVisible = isPasswordVisible,
                    onValueChange = onPasswordValueChange,
                    placeholder = { Text(text = stringResource(R.string.password)) },
                    error = passwordError,
                    showError = showPasswordError,
                    imeAction = ImeAction.Next,
                    onTogglePasswordVisibility = onTogglePasswordVisibility
                )
                AuthPasswordInput(
                    value = confirmPasswordValue,
                    isPasswordVisible = isPasswordVisible,
                    onValueChange = onConfirmPasswordValueChange,
                    placeholder = { Text(text = stringResource(R.string.confirm_password)) },
                    error = confirmPasswordError,
                    showError = showConfirmPasswordError,
                    imeAction = ImeAction.Done,
                    onTogglePasswordVisibility = onTogglePasswordVisibility
                )
                AuthActionButton(
                    buttonText = R.string.sign_up,
                    isLoading = signUpUiState is SignUpUiState.Loading,
                    isSuccess = signUpUiState is SignUpUiState.Success,
                    onClick = onSignUpClick,
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary,
                    )
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(R.string.or_sign_up_with),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FooterButton(
                        onClick = {},
                        icon = R.drawable.ic_google,
                        label = R.string.google
                    )
                    FooterButton(
                        onClick = {},
                        icon = appleLogo,
                        label = R.string.apple
                    )
                    FooterButton(
                        onClick = {},
                        icon = R.drawable.ic_facebook,
                        label = R.string.facebook
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.already_have_account),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        modifier = Modifier.clickable { onNavigateToLoginScreen() },
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenContentPreview() {
    ChatEaseTheme() {
        Column(modifier = Modifier.padding(16.dp)) {
            SignUpScreenContent(
                fullNameValue = "Full name",
                onFullNameValueChange = {},
                fullNameError = 1,
                showFullNameError = false,
                emailValue = "Email",
                onEmailFieldChange = {},
                emailError = 1,
                showEmailError = false,
                passwordValue = "Password",
                onPasswordValueChange = {},
                passwordError = 1,
                showPasswordError = false,
                isPasswordVisible = false,
                onTogglePasswordVisibility = {},
                confirmPasswordValue = "Password",
                onConfirmPasswordValueChange = {},
                confirmPasswordError = 1,
                showConfirmPasswordError = false,
                onSignUpClick = {},
                onNavigateToLoginScreen = {},
                signUpUiState = SignUpUiState.Loading
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun SignUpScreenContentTabletPreview() {
    ChatEaseTheme() {
        Column(modifier = Modifier.padding(16.dp)) {
            SignUpScreenContent(
                fullNameValue = "Full name",
                onFullNameValueChange = {},
                fullNameError = 1,
                showFullNameError = false,
                emailValue = "Email",
                onEmailFieldChange = {},
                emailError = 1,
                showEmailError = false,
                passwordValue = "Password",
                onPasswordValueChange = {},
                passwordError = 1,
                showPasswordError = false,
                isPasswordVisible = false,
                onTogglePasswordVisibility = {},
                confirmPasswordValue = "Password",
                onConfirmPasswordValueChange = {},
                confirmPasswordError = 1,
                showConfirmPasswordError = false,
                onSignUpClick = {},
                onNavigateToLoginScreen = {},
                signUpUiState = SignUpUiState.Loading,
            )
        }
    }
}